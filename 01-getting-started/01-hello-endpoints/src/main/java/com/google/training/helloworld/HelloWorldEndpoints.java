package com.google.training.helloworld;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;

/**
 * A class that defines endpoint functions APIs.
 * NB: Endpoints functions can't return String objects.
 *     They have to return objects with fields!
 */
@Api(name = "helloworldendpoints",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE },
        clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID },
        description = "API for hello world endpoints.")
public class HelloWorldEndpoints {

    /**
     * Return a generic greeting to the world
     * @return generic greeting
     */
    @ApiMethod(name = "sayHello",
                path = "sayHello",
                httpMethod = HttpMethod.GET)
    public HelloClass sayHello() {
        // The @ApiMethod notation declares this method as
        // a method availale externally through Endpoints.
        // And, although this method only outputs a simple greeting,
        // it can't return a String object, and thus the use of the HelloClass!
        return new HelloClass();
    }

    /**
     * Return a specific greeting to the caller
     * @param name the name of the caller
     * @return specific greeting
     */
    @ApiMethod(name = "sayHelloByName",
                path = "sayHelloByName",
                httpMethod = HttpMethod.GET)
    public HelloClass sayHelloByName(@Named("name") String name) {
        // The @ApiMethod notation declares this method as
        // a method availale externally through Endpoints.
        // And, although this method only outputs a simple greeting,
        // it can't return a String object, and thus the use of the HelloClass!
        return new HelloClass(name);
    }

    /**
     * Return a specific greeting including the period of day to the caller
     * @param name the caller name
     * @param period the time of day
     * @return a specific greeting
     */
    @ApiMethod(name = "greetByPeriod",
                path= "greetByPeriod",
                httpMethod = HttpMethod.GET)
    public HelloClass greetByPeriod(@Named("name") String name, @Named("period") String period) {
        return new HelloClass(name, period);
    }
}
