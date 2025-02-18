package edu.ncsu.csc.CoffeeMaker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.ncsu.csc.CoffeeMaker.models.user.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Code inspired by
 * https://www.codejava.net/frameworks/spring-boot/spring-boot-security-authentication-with-jpa-hibernate-and-mysql
 *
 * Allows Spring Security to lookup attempted authentication users in the db.
 *
 * @author Maciej Pruchnik mpruchn
 */
@Component
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    /** Wires access to the user table in the database */
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername ( String username ) throws UsernameNotFoundException {
        User user = userRepository.findByName( username );

        if ( user == null ) {
            throw new UsernameNotFoundException( "Could not find user" );
        }

        return new MyUserDetails( user );
    }

}
