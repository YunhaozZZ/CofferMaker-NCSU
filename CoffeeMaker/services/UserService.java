package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.user.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

import java.util.List;

/**
 *
 * The UserService is used to handle CRUD operations on the User model.
 *
 * @author Sammy Shea (sgshea)
 */
@Component
@Transactional
public class UserService extends Service<User, Long> {

    /**
     * userRepository, to be autowired in by Spring and provide CRUD operations on
     * User model.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Returns the user repository
     *
     * @return Returns the user repository
     */
    @Override
    protected JpaRepository<User, Long> getRepository () {
        return userRepository;
    }

    /**
     * Find a user with the provided name
     *
     * @param name
     *            Name of the user to find
     * @return found user, null if none
     */
    public User findByName ( final String name ) {
        return userRepository.findByName( name );
    }

    /**
     * Finds all users with the provided role
     * @param role
     *              role to find
     * @return found users with role
     */
    public List<User> findByRole(String role) {
        return userRepository.findByRoleContainingIgnoreCase(role);
    }
}
