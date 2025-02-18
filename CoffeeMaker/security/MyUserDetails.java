package edu.ncsu.csc.CoffeeMaker.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import edu.ncsu.csc.CoffeeMaker.models.user.User;

/**
 * Code inspired by
 * https://www.codejava.net/frameworks/spring-boot/spring-boot-security-authentication-with-jpa-hibernate-and-mysql
 *
 * Adapter for Spring security to convert between a JPA User class and
 * authentication requirements.
 *
 * @author Maciej Pruchnik mpruchn
 */
public class MyUserDetails implements UserDetails {
    private final User user;

    public MyUserDetails ( User user ) {
        this.user = user;
    }

    @Override
    public Collection< ? extends GrantedAuthority> getAuthorities () {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority( user.getUsername() );
        return List.of( authority );
    }

    @Override
    public String getPassword () {
        return user.getPassword();
    }

    @Override
    public String getUsername () {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired () {
        return true;
    }

    @Override
    public boolean isAccountNonLocked () {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return true;
    }

    @Override
    public boolean isEnabled () {
        return true;
    }

}
