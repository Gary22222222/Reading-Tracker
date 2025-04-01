package com.group20.dailyreadingtracker;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.mail.javamail.JavaMailSender;

import com.group20.dailyreadingtracker.auth.EmailService;
import com.group20.dailyreadingtracker.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private User testUser;

    @BeforeEach
    public void setup(){
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
    }

    @Test
    void testSendVerificationEmailSuccess() throws MessagingException, UnsupportedEncodingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        String verificationUrl = "http://localhost/verify?token=abc123";

        boolean result = emailService.sendVerificationEmail(testUser, verificationUrl);

        assertTrue(result);
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendVerificationEmailException() throws MessagingException, UnsupportedEncodingException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MessagingException("Test exception")).when(mailSender).send(any(MimeMessage.class));
        String verificationUrl = "http://localhost/verify?token=abc123";

        boolean result = emailService.sendVerificationEmail(testUser, verificationUrl);

        assertFalse(result);
    }

}
