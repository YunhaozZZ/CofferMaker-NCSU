package edu.ncsu.csc.CoffeeMaker.security;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.ncsu.csc.CoffeeMaker.models.user.DefaultUser;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * The security configuration for the web application.
 *
 * Parts of the code inspired by
 * https://www.codejava.net/frameworks/spring-boot/spring-boot-security-authentication-with-jpa-hibernate-and-mysql
 *
 * @author Maciej Pruchnik mpruchn
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties props;

    @Autowired
    private UserService        userService;

    private static void authorizeMethodEndpoints ( //
            final HttpSecurity http, //
            final HttpMethod httpMethod, //
            final List<String> allowedEndpoints, //
            final Set<String> roleNames //
    ) throws Exception {
        if ( allowedEndpoints == null || allowedEndpoints.isEmpty() ) {
            return;
        }
        http.authorizeRequests().antMatchers( //
                httpMethod, //
                allowedEndpoints.toArray( String[]::new ) //
        ).hasAnyAuthority( roleNames.toArray( String[]::new ) ); //
    }

    private static List<Map.Entry<String, Set<String>>> sortAuthoritiesList (
            final Map<String, Set<String>> authorityList ) {
        List<Map.Entry<String, Set<String>>> sortedAuthoritiesList = new ArrayList<>( authorityList.entrySet() );
        sortedAuthoritiesList.sort( Comparator
                .comparingInt( ( Map.Entry<String, Set<String>> stringListEntry ) -> stringListEntry.getValue().size() )
                .reversed() );
        return sortedAuthoritiesList;
    }

    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {
        http.formLogin().loginPage( "/login" ).permitAll() /* */
                .and() //
                .logout().permitAll();

        Map<String, Set<String>> authorityList = new HashMap<>();
        initializeAuthorityList( authorityList );

        for ( SecurityProperties.Role role : props.getRoles() )
            recursivelyAddRolesToAuthorityList( role.getName(), authorityList, role.getInherits() );

        List<Map.Entry<String, Set<String>>> sortedAuthoritiesList = sortAuthoritiesList( authorityList );
        for ( Map.Entry<String, Set<String>> entry : sortedAuthoritiesList ) {
            final SecurityProperties.Role role = getRoleFromName( entry.getKey() );
            final Set<String> roleNames = authorityList.get( role.getName() );

            authorizeMethodEndpoints( http, HttpMethod.GET, role.getAccessiblePages(), roleNames );
            authorizeMethodEndpoints( http, HttpMethod.GET, role.getAllowedGetEndpoints(), roleNames );
            authorizeMethodEndpoints( http, HttpMethod.PUT, role.getAllowedPutEndpoints(), roleNames );
            authorizeMethodEndpoints( http, HttpMethod.POST, role.getAllowedPostEndpoints(), roleNames );
            authorizeMethodEndpoints( http, HttpMethod.DELETE, role.getAllowedDeleteEndpoints(), roleNames );
        }

    }

    private void initializeAuthorityList ( final Map<String, Set<String>> authorityList ) {
        props.getRoles().forEach( role -> {
            final Set<String> authorities = new HashSet<>();
            authorities.add( role.getName() );
            authorityList.put( role.getName(), authorities );
        } );
    }

    private void recursivelyAddRolesToAuthorityList ( //
            final String nameToAuthorize, //
            final Map<String, Set<String>> authorityList, //
            final List<String> inheritedRoles //
    ) throws Exception {
        for ( String roleName : inheritedRoles ) {
            if ( nameToAuthorize.equals( roleName ) )
                throw new Exception( roleName + " role is looped in config" );

            final Set<String> authorities = authorityList.get( roleName );
            authorities.add( nameToAuthorize );
            authorityList.put( roleName, authorities );

            recursivelyAddRolesToAuthorityList( //
                    nameToAuthorize, //
                    authorityList, //
                    getRoleFromName( roleName ).getInherits() //
            );
        }
    }

    private SecurityProperties.Role getRoleFromName ( final String inheritedRoleName ) throws Exception {
        final List<SecurityProperties.Role> inheritedRoleOption = props.getRoles() //
                .stream() //
                .filter( possibleRole -> possibleRole.getName().equals( inheritedRoleName ) ) //
                .collect( Collectors.toList() );
        if ( inheritedRoleOption.isEmpty() )
            throw new Exception( "Non-existent role " + inheritedRoleName );
        if ( inheritedRoleOption.size() > 1 )
            throw new Exception( "Multiple of same role: " + inheritedRoleName );
        return inheritedRoleOption.get( 0 );
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService () {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider () {
        userService.deleteAll();

        props.getRoles().stream().filter( role -> role.getDefaultPassword() != null ).forEach( role -> {
            userService.save( new DefaultUser( role.getName(), //
                    passwordEncoder().encode( role.getDefaultPassword() ), //
                    role.getName() //
            ) );
        } );

        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService( userDetailsService() );
        authProvider.setPasswordEncoder( passwordEncoder() );

        return authProvider;
    }

    @Override
    protected void configure ( final AuthenticationManagerBuilder auth ) {
        auth.authenticationProvider( authenticationProvider() );
    }
}
