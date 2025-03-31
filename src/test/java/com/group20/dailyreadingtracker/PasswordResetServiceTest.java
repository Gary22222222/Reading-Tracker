package com.group20.dailyreadingtracker;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group20.dailyreadingtracker.auth.EmailService;
import com.group20.dailyreadingtracker.auth.PasswordResetService;
import com.group20.dailyreadingtracker.auth.PasswordResetToken;
import com.group20.dailyreadingtracker.auth.PasswordResetTokenRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;
    private PasswordResetToken validToken;
    private PasswordResetToken expiredToken;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("oldPassword");

        validToken = new PasswordResetToken(UUID.randomUUID().toString(), testUser);
        expiredToken = new PasswordResetToken(UUID.randomUUID().toString(), testUser);
        expiredToken.setExpirationTime(LocalDateTime.now().minusHours(1));
    }

    @Test
    void testResetPasswordValidInput() {
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        passwordResetService.resetPassword(testUser, "newPassword");

        verify(userRepository).save(testUser);
        assertEquals("encodedPassword", testUser.getPassword());
    }

    @Test
    void testCreatePasswordResetTokenForUserSuccess() {
        when(tokenRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(validToken);

        passwordResetService.createPasswordResetTokenForUser(testUser, "token123");

        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void testValidatePasswordResetToken() {
        when(tokenRepository.findByToken(validToken.getToken())).thenReturn(Optional.of(validToken));

        String result = passwordResetService.validatePasswordResetToken(validToken.getToken());

        assertEquals("valid", result);
    }

    @Test
    void testValidatePasswordResetTokenExpired() {
        when(tokenRepository.findByToken(expiredToken.getToken())).thenReturn(Optional.of(expiredToken));

        String result = passwordResetService.validatePasswordResetToken(expiredToken.getToken());

        assertEquals("Link already expired, resend link", result);
    }

    @Test
    void testValidatePasswordResetTokenInvalid() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        String result = passwordResetService.validatePasswordResetToken("invalidToken");

        assertEquals("Invalid password reset token", result);
    }

    @Test
    void testProcessPasswordReset() {
        when(tokenRepository.findByToken(validToken.getToken())).thenReturn(Optional.of(validToken));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        String result = passwordResetService.processPasswordReset(
            validToken.getToken(), "newPassword", "newPassword", redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(userRepository).save(testUser);
        verify(tokenRepository).delete(validToken);
        verify(redirectAttributes).addFlashAttribute("success", "Password reset successfully");
    }

    @Test
    void testRequestPasswordReset() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/reset"));
        when(request.getServletPath()).thenReturn("/reset");

        var response = passwordResetService.requestPasswordReset("test@example.com", request);

        assertEquals(200, response.getStatusCode());
        verify(emailService).sendPasswordResetEmail(testUser, anyString());
    }

    @Test
    void testFindUserByPasswordToken() {
        when(tokenRepository.findByToken(validToken.getToken())).thenReturn(Optional.of(validToken));

        Optional<User> result = passwordResetService.findUserByPasswordToken(validToken.getToken());

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    
}
