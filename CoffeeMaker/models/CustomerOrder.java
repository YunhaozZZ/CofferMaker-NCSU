package edu.ncsu.csc.CoffeeMaker.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import edu.ncsu.csc.CoffeeMaker.models.enums.OrderEnum;

/**
 * State and behaviors for the Order class
 *
 * @author Jaden Abrams
 *
 */
@Entity
public class CustomerOrder extends DomainObject {

    /** The unique ID for the order */
    @Id
    @GeneratedValue
    private Long         id;

    /** The time the order was placed */
    private String       timestamp;

    /** The log of order transitions */
    @ElementCollection
    private List<String> log;

    /** whether the recipe has enough ingredients or not */
    private boolean      enoughIngredients;

    /** The order status */
    private OrderEnum    status;

    /** the staff member completing the order */
    private String       barista;

    /** The customer that placed the order */
    private String       customer;

    /** The recipe for the order */
    @OneToOne
    private Recipe       recipe;

    /** Hibernate constructor */
    public CustomerOrder () {

    }

    /**
     * The constructor for a new Order NOTE: This does not set the customer
     * order NOTE: this does not set the order within the customer. To do that,
     * complete customer.setOrder after this call.
     *
     * @param customer
     *            the customer who placed the order
     * @param recipe
     *            the order recipe
     */
    public CustomerOrder ( String customer, Recipe recipe ) {
        this.customer = customer;
        this.recipe = recipe;
        barista = null;
        timestamp = Instant.now().toString();
        log = new ArrayList<String>();
        enoughIngredients = true;
        status = OrderEnum.IN_QUEUE;
        addToLog( null, OrderEnum.IN_QUEUE );
    }

    /**
     * Gets the order log
     *
     * @return the order log
     */
    public List<String> getOrderLog () {
        return log;
    }

    /**
     * Checks a change in the order state
     *
     * @param update
     *            the new order state
     * @return if the change in state is valid
     */
    public boolean updateOrder ( OrderEnum update ) {
        final OrderEnum prev = status;
        // handles transition starting in cancelled or picked up
        if ( update == status || status == OrderEnum.CANCELLED || status == OrderEnum.PICKED_UP ) {
            return false;
        }
        // handles transitions starting in completed
        if ( status == OrderEnum.COMPLETED ) {
            if ( update != OrderEnum.PICKED_UP ) {
                return false;
            }
            status = OrderEnum.PICKED_UP;
            return true;
        }
        // handles transitions starting in in queue
        if ( status == OrderEnum.IN_QUEUE ) {
            if ( update == OrderEnum.IN_PROGRESS || update == OrderEnum.NOT_ENOUGH_INGREDIENTS
                    || update == OrderEnum.CANCELLED ) {
                status = update;
                addToLog( prev, status );
                if ( update == OrderEnum.NOT_ENOUGH_INGREDIENTS ) {
                    enoughIngredients = false;
                }
                return true;
            }
        }
        // handles transitions starting in in progress
        if ( status == OrderEnum.IN_PROGRESS ) {
            if ( update == OrderEnum.IN_QUEUE || update == OrderEnum.NOT_ENOUGH_INGREDIENTS
                    || update == OrderEnum.CANCELLED || update == OrderEnum.COMPLETED ) {
                status = update;
                addToLog( prev, status );
                barista = null;
                if ( update == OrderEnum.NOT_ENOUGH_INGREDIENTS ) {
                    enoughIngredients = false;
                }
                return true;
            }
        }
        // handles transitions starting in not enough ingredients
        if ( status == OrderEnum.NOT_ENOUGH_INGREDIENTS ) {
            if ( update == OrderEnum.CANCELLED || update == OrderEnum.IN_QUEUE || update == OrderEnum.IN_PROGRESS ) {
                status = update;
                addToLog( prev, status );
                if ( update == OrderEnum.CANCELLED || update == OrderEnum.IN_QUEUE ) {
                    barista = null;
                }
                enoughIngredients = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Helper for adding to the log
     *
     * @param previousState
     *            the previous order state
     * @param update
     *            the new order state
     */
    private void addToLog ( OrderEnum previousState, OrderEnum update ) {
        if ( previousState == null ) {
            log.add( "Order Created in state " + update.name() + " by " + customer + " at " + timestamp.toString() );
        }
        else {
            log.add( previousState.name() + " => " + update.name() + " at " + Instant.now() );
        }
    }

    /**
     * Gets the customer
     *
     * @return customer
     */
    public String getCustomer () {
        return customer;
    }

    /**
     * gets the barista
     *
     * @return barista
     */
    public String getStaff () {
        return barista;
    }

    /**
     * sets the staff member and makes the appropriate transition
     *
     * @param staff
     *            staff member
     * @return if the staff member was set successfully
     */
    public boolean setStaffMember ( String staff ) {
        if ( staff == null ) {
            barista = null;
            return updateOrder( OrderEnum.IN_QUEUE );
        }
        if ( barista == null ) {
            barista = staff;
            return updateOrder( OrderEnum.IN_PROGRESS );
        }
        return false;
    }

    /**
     * gets the order recipe
     *
     * @return the recipe
     */
    public Recipe getRecipe () {
        return recipe;
    }

    /**
     * Checks if recipe has enough ingredients
     *
     * @return the enoughIngredients boolean
     */
    public boolean checkIfEnoughIngredients () {
        return enoughIngredients;
    }

    /**
     * Gets the ID
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * returns the order status
     *
     * @return status
     */
    public OrderEnum getCurrentState () {
        return status;
    }

    /**
     * Gets the timestamp
     *
     * @return timestamp
     */
    public String getTimestamp () {
        return timestamp;
    }

    @Override
    public boolean equals ( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final CustomerOrder other = (CustomerOrder) obj;
        System.out.println( Objects.equals( barista, other.barista ) && Objects.equals( customer, other.customer )
                && enoughIngredients == other.enoughIngredients && Objects.equals( log, other.log )
                && Objects.equals( recipe, other.recipe ) && status == other.status
                && Objects.equals( timestamp, other.timestamp ) && Objects.equals( id, other.id ) );
        return Objects.equals( barista, other.barista ) && Objects.equals( customer, other.customer )
                && enoughIngredients == other.enoughIngredients && Objects.equals( log, other.log )
                && Objects.equals( recipe, other.recipe ) && status == other.status
                && Objects.equals( timestamp, other.timestamp ) && Objects.equals( id, other.id );
    }

    @Override
    public String toString () {
        return "CustomerOrder [id=" + id + ", timestamp=" + timestamp + ", enoughIngredients=" + enoughIngredients
                + ", status=" + status + ", barista=" + barista + ", customer=" + customer + ", recipe=" + recipe + "]";
    }

}
