package edu.ncsu.csc.CoffeeMaker.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.CustomerOrder;
import edu.ncsu.csc.CoffeeMaker.models.enums.OrderEnum;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 *
 * The OrderService is used to handle CRUD operations on the Order model.
 *
 * @author Sammy Shea (sgshea)
 */
@Component
@Transactional
public class OrderService extends Service<CustomerOrder, Long> {

    /**
     * OrderService, to be autowired in by Spring and provide CRUD operations on
     * Order model.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Returns the order repository
     *
     * @return Returns the order repository
     */
    @Override
    protected JpaRepository<CustomerOrder, Long> getRepository () {
        return orderRepository;
    }

    /**
     * Gets all the orders which are unclaimed
     *
     * @return found Orders as list
     */
    public List<CustomerOrder> getUnclaimedOrders () {
        return orderRepository.findByStatus( OrderEnum.IN_QUEUE );
    }

    /**
     * Gets all the orders under a customer
     *
     * @param customer
     *            Customer whose orders to get
     * @return found Orders as list
     */
    public List<CustomerOrder> getOrdersByCustomer ( final String customer ) {
        return orderRepository.findOrdersByCustomer( customer );
    }

    /**
     * Gets all the orders in order of creation date
     *
     * @return found Orders as list
     */
    public List<CustomerOrder> ordersByTimestamp () {
        return orderRepository.findAllByOrderByTimestamp();
    }
}
