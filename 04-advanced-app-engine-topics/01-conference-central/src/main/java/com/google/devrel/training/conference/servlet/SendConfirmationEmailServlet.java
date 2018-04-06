package com.google.devrel.training.conference.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet for sending out email confirmations
 * when a conference is created, using task queues.
 * @author Solange U. Gasengayire
 */
public class SendConfirmationEmailServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: use a task to send confirmation emails when someone creates a conference
    }
}
