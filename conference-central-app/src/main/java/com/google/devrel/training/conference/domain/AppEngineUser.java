package com.google.devrel.training.conference.domain;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * A class to represent a user when injected from an Android mobile app.
 * This compensates for the lack of the userId in Android User objects.
 * @author Solange U. Gasengayire
 */
@Entity
public class AppEngineUser {

    @Id
    private String email;

    private User user;

    /**
     * Default no-argument constructor
     */
    private AppEngineUser() {}

    /**
     * Constructor with arguments
     * @param user the current Android user
     */
    public AppEngineUser(User user) {
        this.user = user;
        this.email = user.getEmail();
    }

    /**
     * Generate an Objectify Key for this entity
     * @return entity key
     */
    public Key<AppEngineUser> getKey() {
        return Key.create(AppEngineUser.class, email);
    }

    /**
     * Return the user object
     * @return user
     */
    public User getUser() {
        return this.user;
    }
}
