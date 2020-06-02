package hu.bme.aut.payroll.web.service.email;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Represents service that responsible for sending emails
 */
@Service
public class EmailService {

    public final JavaMailSender emailSender;

    public EmailService(@Qualifier("getJavaMailSender") JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Sends an email to provided email address
     * @param from the origin of the email
     * @param to the recipient of the email
     * @param subject the subject of the email
     * @param text the text of the email
     */
    @Async
    public void sendEmail(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
