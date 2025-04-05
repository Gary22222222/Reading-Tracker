package com.group20.dailyreadingtracker;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group20.dailyreadingtracker.auth.EmailService;
import com.group20.dailyreadingtracker.auth.PasswordResetService;
import com.group20.dailyreadingtracker.auth.PasswordResetToken;
import com.group20.dailyreadingtracker.auth.PasswordResetTokenRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {
    
    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;
    private PasswordResetToken testToken;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("oldPassword");
        
        testToken = new PasswordResetToken();
        testToken.setToken("testToken");
        testToken.setUser(testUser);
        testToken.setExpirationTime(LocalDateTime.now().plusHours(1));
    }

    @Test
    void testResetPasswordValidInput() {
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");
        
        passwordResetService.resetPassword(testUser, "newPassword");
        
        assertEquals("encodedNewPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    @Transactional
    void testCreatePasswordResetTokenForUserSuccess() {
        when(tokenRepository.findByUser(testUser)).thenReturn(Optional.empty());
        
        passwordResetService.createPasswordResetTokenForUser(testUser, "newToken");
        
        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void testValidatePasswordResetToken() {
        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(testToken));
        
        String result = passwordResetService.validatePasswordResetToken("validToken");
        
        assertEquals("valid", result);
    }

    @Test
    void testValidatePasswordResetTokenExpired() {
        testToken.setExpirationTime(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(testToken));
        
        String result = passwordResetService.validatePasswordResetToken("expiredToken");
        
        assertEquals("Link already expired, resend link", result);
    }

    @Test
    void testValidatePasswordResetTokenInvalid() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        String result = passwordResetService.validatePasswordResetToken("invalidToken");

        assertEquals("Invalid password reset token", result);
    }

    @Test
    void testRequestPasswordResetSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tokenRepository.findByUser(testUser)).thenReturn(Optional.empty());
        
        StringBuffer url = new StringBuffer("http://localhost:8080");
        when(request.getRequestURL()).thenReturn(url);
        when(request.getServletPath()).thenReturn("");
        
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        
        ResponseEntity<String> response = passwordResetService.requestPasswordReset("test@example.com", request);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Password reset link sent"));
        
        
        verify(tokenRepository).save(tokenCaptor.capture());
        String generatedToken = tokenCaptor.getValue().getToken();
        
        verify(emailService).sendPasswordResetEmail(
            eq(testUser),
            urlCaptor.capture()
        );
        
        String sentUrl = urlCaptor.getValue();
        assertTrue(sentUrl.startsWith("http://localhost:8080/reset-password?token="));
        assertTrue(sentUrl.endsWith(generatedToken));
        assertEquals(generatedToken.length() + "http://localhost:8080/reset-password?token=".length(), 
                sentUrl.length());
    }

    @Test
    void testInvalidateExistingTokens() {
        when(tokenRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(testToken));
        
        passwordResetService.invalidateExistingTokens("test@example.com");
        
        assertTrue(testToken.getExpirationTime().isBefore(LocalDateTime.now().plusMinutes(1)));
        verify(tokenRepository).save(testToken);
    }

    @Test
    void testFindUserByPasswordToken() {
        when(tokenRepository.findByToken(testToken.getToken())).thenReturn(Optional.of(testToken));

        Optional<User> result = passwordResetService.findUserByPasswordToken(testToken.getToken());

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

}
