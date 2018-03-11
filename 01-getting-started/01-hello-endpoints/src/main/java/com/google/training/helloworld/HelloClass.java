package com.google.training.helloworld;

/**
 * Simple POJO class that generates a greeting
 */
public class HelloClass {

    private String message = "Hello World";

    /**
     * Default constructor
     */
    public HelloClass () {
    }

    /**
     * One-argument constructor
     * @param name the name
     */
    public HelloClass (String name) {
        this.message = "Hello " + name + "!";
    }

    /**
     * Two-argument constructor
     * @param name the name
     * @param period the period
     */
    public HelloClass(String name, String period) {
        this.message = String.format("Good %s %s!", period, name);
    }

    /**
     * Return this greeting's message
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
