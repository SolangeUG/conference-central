package com.google.devrel.training.conference.domain;

/**
 * A simple wrapper for announcement messag
 * @author Solange U. Gasengayire
 */
public class Announcement {

    private String message;

    /**
     * Default constructor
     */
    public Announcement() {}

    /**
     * One-argument constructor
     * @param message the message content
     */
    public Announcement(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
