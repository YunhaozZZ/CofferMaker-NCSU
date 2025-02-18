package edu.ncsu.csc.CoffeeMaker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.CustomerOrder;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderEnum;

/**
 * Interface for the database, used by the Spring Boot framework. Allows saving
 * and retrieving Orders.
 *
 * @author Sammy Shea (sgshea)
 *
 */
public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    /**
     * Finds all orders under a specific customer's name
     *
     * @param customer
     *            object of the customer
     * @return List of orders if found, null if none
     */
    List<CustomerOrder> findOrdersByCustomer ( String customer );

    /**
     * Finds all orders that are of a certain status
     *
     * @param status
     *            to search for
     * @return List of orders if found, null if none
     */
    List<CustomerOrder> findByStatus ( OrderEnum status );

    /**
     * Returns all orders sorted by date ordered ascending
     *
     * @return List of orders if found, null if none
     */
    List<CustomerOrder> findAllByOrderByTimestamp ();
}
