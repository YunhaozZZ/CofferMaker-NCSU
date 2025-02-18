package edu.ncsu.csc.CoffeeMaker.models.user;

import javax.persistence.Entity;

@Entity
public class DefaultUser extends User {
    public DefaultUser ( final String username, final String password, final String role ) {
        super( username, password, role );
    }

    public DefaultUser () {
    }
}
