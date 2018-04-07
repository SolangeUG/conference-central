package com.google.devrel.training.conference.servlet;

import com.google.appengine.api.utils.SystemProperty;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A servlet for sending out email confirmations
 * when a conference is created, using task queues.
 * @author Solange U. Gasengayire
 */
public class SendConfirmationEmailServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(
            SendConfirmationEmailServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // COMPLETED: use a task to send a confirmation email when someone creates a conference
        String email = req.getParameter("email");
        String conferenceInfo = req.getParameter("conferenceInfo");

        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);
        String body = "Hi, you have created the following conference. \n" + conferenceInfo;

        try {

            InternetAddress from = new InternetAddress(
                                        String.format("noreply@%s.appspotmail.com",
                                        SystemProperty.applicationId.get()),
                                        "Conference Central");
            InternetAddress to = new InternetAddress(email, "");

            Message message = new MimeMessage(session);
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, to);
            message.setSubject("You created a new Conference!");
            message.setText(body);
            Transport.send(message);

        } catch (MessagingException exception) {
            LOG.log(Level.WARNING, String.format("Failed to send an email to %s", email), exception);
            throw new ServletException(exception);
        }
    }
}
