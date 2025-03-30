package com.group20.dailyreadingtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.springframework.mail.javamail.JavaMailSender;

import com.group20.dailyreadingtracker.auth.EmailService;
import com.group20.dailyreadingtracker.user.User;

import jakarta.mail.internet.MimeMessage;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private EmailService emailService;

    @Test
    void sendPasswordResetEmail_ConstructsValidEmail() throws Exception {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("Password123");
        
        String resetUrl = "http://example.com/reset?token=abc123";
        
        emailService.sendPasswordResetEmail(user, resetUrl);
        
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        
        MimeMessage message = messageCaptor.getValue();
        assertTrue(message.getContent().toString().contains(resetUrl));
        assertEquals("Password Reset Request", message.getSubject());
        assertEquals("test@mail.com", message.getAllRecipients()[0].toString());
    }
}
