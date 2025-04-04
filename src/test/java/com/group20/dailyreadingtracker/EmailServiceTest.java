package com.group20.dailyreadingtracker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.group20.dailyreadingtracker.auth.EmailService;
import com.group20.dailyreadingtracker.user.User;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
    }

    @Test
    public void sendVerificationEmailSuccess() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        String verificationUrl = "http://verify?token=abc123";

        boolean result = emailService.sendVerificationEmail(testUser, verificationUrl);

        assertTrue(result);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    public void sendPasswordResetEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        String resetUrl = "http://reset?token=xyz789";

        assertDoesNotThrow(() -> 
            emailService.sendPasswordResetEmail(testUser, resetUrl));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    public void sendHtmlEmail() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        String to = "test@example.com";
        String subject = "Test Subject";
        String htmlContent = "<html><body>Test</body></html>";

        emailService.sendHtmlEmail(to, subject, htmlContent);

        verify(mailSender).send(mimeMessage);
    }
}
