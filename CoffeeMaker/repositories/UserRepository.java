package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.ncsu.csc.CoffeeMaker.models.user.User;

import java.util.List;

/**
 * Code inspired by
 * https://www.codejava.net/frameworks/spring-boot/spring-boot-security-authentication-with-jpa-hibernate-and-mysql
 *
 * Interface for the database, used by the Spring Boot framework. Allows saving
 * and retrieving users.
 *
 * @author Maciej Pruchnik mpruchn
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by its name
     * 
     * @param name
     *            name of the user
     * @return found user
     */
    @Query ( "SELECT u FROM User u WHERE u.username = :name" )
    public User findByName ( @Param ( "name" ) String name );

    /**
     * Finds all users with the provided role
     * @param role
     *              role to find
     * @return found users with role
     */
    public List<User> findByRoleContainingIgnoreCase(String role);
}
