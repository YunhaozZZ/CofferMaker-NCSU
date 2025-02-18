package edu.ncsu.csc.CoffeeMaker.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.controllers.APIController;

/**
 * Allows for configuring of all properties contained in this class in
 * application.yml
 *
 * @author Maciej Pruchnik mpruchn
 */
@Component
@ConfigurationProperties ( prefix = "security" )
public class SecurityProperties {
    private List<Role> roles;

    public List<Role> getRoles () {
        return roles;
    }

    public void setRoles ( final List<Role> roles ) {
        this.roles = roles;
    }

    public static class Role {
        private String       name;
        private String       defaultPassword;
        private String       homePage;
        private List<String> inherits;
        private List<String> accessiblePages;
        private List<String> allowedGetEndpoints;
        private List<String> allowedPutEndpoints;
        private List<String> allowedPostEndpoints;
        private List<String> allowedDeleteEndpoints;

        private static List<String> appendApiBasePathToEndpoints ( final List<String> clientAccessibleGetEndpoints ) {
            return clientAccessibleGetEndpoints == null ? null
                    : clientAccessibleGetEndpoints.stream() //
                            .map( endpoint -> APIController.BASE_PATH + endpoint ) //
                            .collect( Collectors.toList() );
        }

        public List<String> getInherits () {
            return inherits == null ? new ArrayList<>() : inherits;
        }

        public void setInherits ( final List<String> inherits ) {
            this.inherits = inherits;
        }

        public String getName () {
            return name;
        }

        public void setName ( final String name ) {
            this.name = name;
        }

        public String getDefaultPassword () {
            return defaultPassword;
        }

        public void setDefaultPassword ( final String defaultPassword ) {
            this.defaultPassword = defaultPassword;
        }

        public String getHomePage () {
            return homePage;
        }

        public void setHomePage ( final String homePage ) {
            this.homePage = homePage;
        }

        public List<String> getAccessiblePages () {
            return accessiblePages;
        }

        public void setAccessiblePages ( final List<String> accessiblePages ) {
            this.accessiblePages = accessiblePages;
        }

        public List<String> getAllowedGetEndpoints () {
            return allowedGetEndpoints;
        }

        public void setAllowedGetEndpoints ( final List<String> allowedGetEndpoints ) {
            this.allowedGetEndpoints = appendApiBasePathToEndpoints( allowedGetEndpoints );
        }

        public List<String> getAllowedPutEndpoints () {
            return allowedPutEndpoints;
        }

        public void setAllowedPutEndpoints ( final List<String> allowedPutEndpoints ) {
            this.allowedPutEndpoints = appendApiBasePathToEndpoints( allowedPutEndpoints );
        }

        public List<String> getAllowedPostEndpoints () {
            return allowedPostEndpoints;
        }

        public void setAllowedPostEndpoints ( final List<String> allowedPushEndpoints ) {
            this.allowedPostEndpoints = appendApiBasePathToEndpoints( allowedPushEndpoints );
        }

        public List<String> getAllowedDeleteEndpoints () {
            return allowedDeleteEndpoints;
        }

        public void setAllowedDeleteEndpoints ( final List<String> allowedDeleteEndpoints ) {
            this.allowedDeleteEndpoints = allowedDeleteEndpoints;
        }
    }
}
