package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.CustomerOrder;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderEnum;
import edu.ncsu.csc.CoffeeMaker.models.user.User;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller for all orders API functions.
 *
 * @author Sammy Shea (sgshea)
 */
@RestController
@RequestMapping ( "/api/v1/orders" )
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIOrdersController extends APIController {

    /**
     * OrdersService object, to be autowired in by Spring to allow for manipulating
     * the Orders model
     */
    @Autowired
    private OrderService  ordersService;

    /**
     * CustomerService object, to be autowired in by Spring to allow for
     * manipulating the Customers model
     */
    @Autowired

    private UserService   userService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for manipulating
     * the Recipe model
     */
    @Autowired
    private RecipeService recipeService;

    /**
     * Creates a new order using the object passed in.
     *
     * @param auth
     *            authentication token, to get user
     * @param json
     *            json formatted data, key value of string: long
     * @return response entity with 200 status
     */

    @PostMapping
    public ResponseEntity createOrder ( @AuthenticationPrincipal final Authentication auth,
            @RequestBody final Map<String, String> json ) {

        final User customer = userService.findByName( auth.getName() );
        if ( customer == null ) {
            return new ResponseEntity( errorResponse( "customer " + auth.getName() + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }
        final Recipe recipe = recipeService.findByName( json.get( "recipe" ) );

        if ( recipe == null ) {
            return new ResponseEntity( errorResponse( "recipe " + json.get( "recipe" ) + " could not be found" ),
                    HttpStatus.NOT_FOUND );
        }

        final CustomerOrder orders = customer.placeOrder( recipe );
        System.out.println( "Hit!" );
        ordersService.save( orders );
        System.out.println( "The system has " + ordersService.count() + " orders" );
        return new ResponseEntity(
                successResponse( "New order by " + customer.getUsername() + " successfully created" ), HttpStatus.OK );
    }

    /**
     * Gets all orders within the database. Returns them in sorted order by creation
     * date
     *
     * @return all orders as list
     */

    @GetMapping
    public ResponseEntity getOrders () {

        final List<CustomerOrder> orders = ordersService.ordersByTimestamp();
        return null == orders
                ? new ResponseEntity( errorResponse( "Error returning all orders" ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( orders, HttpStatus.OK );
    }

    /**
     * Gets all unclaimed orders within the database.
     *
     * @return all orders as list
     */
    @GetMapping ( "/unclaimed" )
    public ResponseEntity getUnclaimedOrders () {
        final List<CustomerOrder> orders = ordersService.getUnclaimedOrders();

        return null == orders
                ? new ResponseEntity( errorResponse( "Error returning all orders" ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( orders, HttpStatus.OK );
    }

    /**
     * Gets all the orders under a specific customer
     *
     * @param auth
     *            the authenication which called this endpoint
     * @return orders as list
     */

    @GetMapping ( "/myorders" )
    public ResponseEntity getOrdersByCustomer ( @AuthenticationPrincipal Authentication auth ) {
        final List<CustomerOrder> orders = ordersService.getOrdersByCustomer( auth.getName() );
        return null == orders
                ? new ResponseEntity( errorResponse( "No orders under " + auth.getName() + " name" ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( orders, HttpStatus.OK );
    }

    /**
     * Updates an order given its id and the new status
     *
     * @param id
     *            id of the order to update
     * @param status
     *
     * @return 200 HTTP response entity if successful, 404 if order does not exist,
     *         416 if status is wrong
     */
    @PutMapping ( "/{id}/{status}" )
    public ResponseEntity updateOrderStatus ( @PathVariable final Long id, @PathVariable final String status ) {
        final OrderEnum e = OrderEnum.valueOf( status );
        // Attempt to find order

        final CustomerOrder db = ordersService.findById( id );
        if ( db == null ) {
            return new ResponseEntity( errorResponse( "No order found: " + id.toString() ), HttpStatus.NOT_FOUND );
        }

        // attempt to update the order, if it fails (returns false) tell the
        // frontend
        if ( !db.updateOrder( e ) ) {
            return new ResponseEntity( errorResponse( "This status [" + status + "] is not a valid transition." ),
                    HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE );
        }

        ordersService.save( db );
        return new ResponseEntity( successResponse( id.toString() + " updated successfully" ), HttpStatus.OK );
    }

    /**
     * Updates an order given its id and the new staff owner
     *
     * @param id
     *            id of the order to update
     * @param auth
     *            The authentication which called this endpoint
     * 
     * @return 200 HTTP response entity if successful, 404 if order does not exist
     */
    @PutMapping ( "/staff/{id}" )
    public ResponseEntity updateOrderStaff ( @AuthenticationPrincipal final Authentication auth,
            @PathVariable final Long id ) {
        // Update order by id and save it to the database
        final CustomerOrder order = ordersService.findById( id );

        final User staff = userService.findByName( auth.getName() );
        if ( order == null ) {
            return new ResponseEntity( errorResponse( "No order found: " + id.toString() ), HttpStatus.NOT_FOUND );
        }
        if ( staff == null ) {
            return new ResponseEntity( errorResponse( "Staff " + auth.getName() + " not found" ),
                    HttpStatus.NOT_FOUND );
        }

        staff.claimOrder( order );
        userService.save( staff );
        ordersService.save( order );

        return new ResponseEntity( successResponse( id.toString() + " updated successfully" ), HttpStatus.OK );
    }

    /**
     * Deletes an order given it's id
     *
     * @param id
     *            id of the order to delete
     * @return 200 HTTP response entity
     */

    @DeleteMapping ( "/{id}" )
    public ResponseEntity deleteOrder ( @PathVariable final Long id ) {
        // Delete order by id from the database
        final CustomerOrder db = ordersService.findById( id );
        if ( db == null ) {
            return new ResponseEntity( errorResponse( "No order found: " + id.toString() ), HttpStatus.NOT_FOUND );
        }

        ordersService.delete( db );

        return new ResponseEntity( successResponse( id + " was deleted successfully" ), HttpStatus.OK );
    }

    @GetMapping ( "/finances" )
    public ResponseEntity getRevenue () {
        final List<CustomerOrder> orders = ordersService.findAll();
        int revenue = 0;
        if ( orders == null ) {
            return new ResponseEntity( errorResponse( "No orders found" ), HttpStatus.NOT_FOUND );
        }
        for ( final CustomerOrder co : orders ) {
            if ( co.getCurrentState() == OrderEnum.COMPLETED || co.getCurrentState() == OrderEnum.PICKED_UP ) {
                revenue += co.getRecipe().getPrice();
            }
        }
        return new ResponseEntity( successResponse( "Current revenue is: " + revenue ), HttpStatus.OK );
    }

}
