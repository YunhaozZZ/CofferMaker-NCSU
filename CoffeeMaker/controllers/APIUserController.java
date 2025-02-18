package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.user.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller for all orders API functions.
 *
 * @author Sammy Shea (sgshea)
 */
@RestController
@RequestMapping ( "/api/v1/users" )
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIUserController extends APIController {

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the Users model
     */
    @Autowired
    private UserService userService;

    /**
     * Base path creates a new CUSTOMER user { username: 'John Smith', password:
     * 'password' }
     *
     * @param customer
     *            user to create
     * @return response entity with 200 status
     */
    @PostMapping
    public ResponseEntity createUser ( @RequestBody User customer ) {
        userService.save( customer );
        return new ResponseEntity( successResponse( "New user " + customer.getUsername() + " successfully created" ),
                HttpStatus.OK );
    }

    /**
     * /staff path creates a new STAFF user Can only be called by a manager {
     * username: 'John Smith', password: 'password' }
     *
     * @param auth
     *            Manager authentication required
     * @param staff
     *            user to create
     * @return response entity with 200 status 401 if no access (not manager)
     */
    @PostMapping ( "/staff" )
    public ResponseEntity createStaff ( @AuthenticationPrincipal Authentication auth, @RequestBody User staff ) {
        if ( auth != null && auth.getAuthorities().stream().anyMatch( a -> a.getAuthority().equals( "manager" ) ) ) {
            userService.save( staff );
            return new ResponseEntity( successResponse( "New user " + staff.getUsername() + " successfully created" ),
                    HttpStatus.OK );
        }
        return new ResponseEntity( errorResponse( "No access to creating staff" ), HttpStatus.UNAUTHORIZED );
    }

    /**
     * Gets all customers within the database
     *
     * @return all customers as list
     */
    @GetMapping
    public List<User> getCustomers () {
        return userService.findByRole( "customer" );
    }

    /**
     * Gets all users within the database
     *
     * @return all users as list
     */
    @GetMapping ( "/all" )
    public List<User> getUsers () {
        return userService.findAll();
    }

    /**
     * Deletes a user
     *
     * @param id
     *            id of user to delete
     * @return 200 HTTP response entity
     */
    @DeleteMapping ( "/{id}" )
    public ResponseEntity deleteUser ( @PathVariable Long id ) {
        final User db = userService.findById( id );
        if ( db == null ) {
            return new ResponseEntity( errorResponse( "No user found: " + id.toString() ), HttpStatus.NOT_FOUND );
        }

        userService.delete( db );
        return new ResponseEntity( successResponse( id + " was deleted successfully" ), HttpStatus.OK );
    }

}
