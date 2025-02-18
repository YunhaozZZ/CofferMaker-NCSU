package edu.ncsu.csc.CoffeeMaker.models.enums;

/**
 * The values for the state machine that dictates CustomerOrder state
 *
 * @author Jaden Abrams
 *
 */
public enum OrderEnum {
    /**
     * The order states IN_QUEUE: The CustomerOrder is in the queue and is not
     * currently being worked on
     */
    IN_QUEUE,

    /**
     * IN_PROGRESS: The CustomerOrder is assigned to a Staff member and is
     * currently being worked on
     */
    IN_PROGRESS,

    /**
     * COMPLETED: The CustomerOrder is ready for pickup
     */
    COMPLETED,

    /**
     * PICKED_UP: The CustomerOrder has been picked up
     */
    PICKED_UP,

    /**
     * NOT_ENOUGH_INGREDIENTS: There are not enough ingredients to fulfill the
     * order and ingredients must be acquired before the CustomerOrder can be
     * worked on
     */
    NOT_ENOUGH_INGREDIENTS,

    /**
     * CANCELLED: The CustomerOrder will not be worked on and the CustomerOrder
     * is to be discarded
     */
    CANCELLED;
}
