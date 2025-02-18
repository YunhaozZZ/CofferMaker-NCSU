package edu.ncsu.csc.CoffeeMaker.models.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import edu.ncsu.csc.CoffeeMaker.models.CustomerOrder;
import edu.ncsu.csc.CoffeeMaker.models.DomainObject;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderEnum;

/**
 * Class inspired by
 * https://www.codejava.net/frameworks/spring-boot/spring-boot-security-authentication-with-jpa-hibernate-and-mysql
 *
 * @author Maciej Pruchnik mpruchn
 */
@Entity
@Table ( name = "users" )
public class User extends DomainObject {

    /**
     * hi
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long               id;

    /** the Staff's current order */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private Set<CustomerOrder> currentOrders;

    @OneToOne
    private CustomerOrder      workingOrder;
    /** the Customer's order history */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private Set<CustomerOrder> orderHistory;

    private String             username;
    private String             password;
    private String             role;
    private boolean            locked;

    public User ( final String username, final String password, final String role ) {
        this( username, password, role, false );
    }

    public User ( final String username, final String password, final String role, final boolean locked ) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.locked = locked;
        orderHistory = new HashSet<CustomerOrder>();
        currentOrders = new HashSet<CustomerOrder>();
    }

    public User () {

    }

    public String getUsername () {
        return username;
    }

    public String getPassword () {
        return password;
    }

    public String getRole () {
        return role;
    }

    public boolean isLocked () {
        return locked;
    }

    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Claim an order in the queue
     *
     * @param orders
     *            the order to claim/set
     * @return true if successful
     */
    public boolean claimOrder ( CustomerOrder orders ) {
        if ( orders.getStaff() != null ) {
            return false;
        }
        final boolean status = orders.setStaffMember( this.getUsername() );
        if ( status ) {
            workingOrder = orders;
        }
        return status;
    }

    /**
     * Set the order as in the queue if it has not been set and clear out the
     * currentOrder field
     *
     * @return if the current order was returned to the queue
     */
    public boolean returnCurrentOrderToQueue () {
        if ( workingOrder == null ) {
            return false;
        }
        workingOrder.setStaffMember( null );
        workingOrder = null;
        return true;
    }

    /**
     * Sets the order to fulfil if it has not been set and clear out the
     * currentOrder field
     *
     * @return if the order was successfully fulfilled
     */
    public boolean fulfillCurrentOrder () {
        if ( workingOrder != null ) {
            final boolean status = workingOrder.updateOrder( OrderEnum.COMPLETED );
            if ( status ) {
                workingOrder = null;
            }
            return status;
        }
        return false;
    }

    /**
     * Cancels an order
     *
     * @return if the order was successfully cancelled
     */
    public boolean cancelOrder () {
        if ( workingOrder != null ) {
            workingOrder.updateOrder( OrderEnum.CANCELLED );
            workingOrder = null;
        }
        return true;

    }

    /**
     * Cancel a particular order for User as Customer
     *
     * @param order
     *            the order to remove
     * @return true if the customer has the order and it is removed, false if it
     *         doesn't have it
     */
    public boolean cancelOrder ( CustomerOrder order ) {
        if ( currentOrders.contains( order ) ) {
            order.updateOrder( OrderEnum.CANCELLED );
            currentOrders.remove( order );
            return true;
        }
        return false;
    }

    /**
     * Creates new recipe for the caller to save
     *
     * @param name
     *            the recipe name
     * @param ingredients
     *            the recipe ingredients
     * @param price
     *            the recipe price
     * @return the newly created recipe (not saved in the database)
     */
    public Recipe createNewRecipe ( String name, List<Ingredient> ingredients, Integer price ) {
        return new Recipe( name, ingredients, price );
    }

    /**
     * Set the customer order
     *
     * @param orders
     *            the order to set
     * @return if the current order has been set
     */
    public boolean setOrder ( CustomerOrder orders ) {
        if ( orders == null ) {
            workingOrder = null;
            return false;
        }
        if ( workingOrder != null ) {
            return false;
        }
        orderHistory.add( orders );
        workingOrder = orders;
        return true;
    }

    /**
     * Places a CustomerOrder and returns the new order
     *
     * @param recipe
     *            the recipe for the order
     * @return the placed CustomerOrder
     */
    public CustomerOrder placeOrder ( Recipe recipe ) {
        final CustomerOrder ret = new CustomerOrder( getUsername(), recipe );
        currentOrders.add( ret );
        return ret;
    }

    /**
     * Gets the customer's current order
     *
     * @return the current order
     */
    public CustomerOrder getCurrentOrder () {
        return workingOrder;
    }

    public Set<CustomerOrder> getPlacedOrders () {
        return currentOrders;
    }

    /**
     * Gets a list of the customer's orders
     *
     * @return the order history
     */
    public Set<CustomerOrder> getOrderHistory () {
        return orderHistory;
    }

    /**
     * The customer picks up their order, sets the currentOrder to null
     *
     * @return if the order was successfully picked up
     */
    public boolean pickupOrder ( CustomerOrder order ) {
        if ( currentOrders.contains( order ) ) {
            final boolean status = order.updateOrder( OrderEnum.PICKED_UP );
            if ( status ) {
                currentOrders.remove( order );
            }
            return status;
        }
        return false;
    }

    @Override
    public String toString () {
        if ( workingOrder == null ) {
            return "User [currentOrder=N/A" + ", id=" + getId() + ", name=" + getUsername() + "]";
        }
        return "User [currentOrder=" + workingOrder.getRecipe().getName() + ", id=" + getId() + ", name()="
                + getUsername() + "]";
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        final User user = (User) o;
        return locked == user.locked && Objects.equals( username, user.username )
                && Objects.equals( password, user.password ) && role == user.role;
    }

    @Override
    public int hashCode () {
        return Objects.hash( username, password, role, locked );
    }

}
