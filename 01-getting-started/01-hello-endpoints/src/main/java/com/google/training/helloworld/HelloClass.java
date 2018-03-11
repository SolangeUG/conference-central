package com.google.training.helloworld;

/**
 * Simple POJO class that generates a greeting
 */
public class HelloClass {

    private String message = "Hello World";

    public HelloClass () {
    }

    public HelloClass (String name) {
        this.message = "Hello " + name + "!";
    }

    public String getMessage() {
        return message;
    }
}
