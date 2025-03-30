package com.group20.dailyreadingtracker.auth;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.group20.dailyreadingtracker.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String senderEmail = "sloooonya@yandex.com";
    private final String senderName = "Daily Reading Tracker";
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(User user, String url) {
        String subject = "Password Reset Request";
        String content = String.format("""
            <html>
                <body>
                    <p>Dear %s,</p>
                    <p>We received a request to reset your password.</p>
                    <p>Click <a href="%s">here</a> to reset your password.</p>
                    <p>If you didn't request this, please ignore this email.</p>
                    <p>Best regards,<br>%s Team</p>
                </body>
            </html>
            """, user.getUsername(), url, senderName);

        try {
            sendHtmlEmail(user.getEmail(), subject, content);
            logger.info("Reset email sent to {} with link: {}", user.getEmail(), url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Failed to send password reset email to {}", user.getEmail(), e);
            throw new EmailException("Failed to send password reset email", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        try {
            helper.setFrom(new InternetAddress(senderEmail, senderName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
        
            mailSender.send(message);
        } catch (UnsupportedEncodingException e) {
            helper.setFrom(senderEmail);
            mailSender.send(message);
            logger.warn("Used simple from address due to encoding issues");
        }
    }

    public String generatePasswordResetUrl(User user, HttpServletRequest request, String token) {
        String baseUrl = request.getRequestURL().toString()
                .replace(request.getServletPath(), ""); 
        return baseUrl + "/reset-password?token=" + token;
    }

    public class EmailException extends RuntimeException {
        public EmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}