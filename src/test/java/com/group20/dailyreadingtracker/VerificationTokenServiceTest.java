package com.group20.dailyreadingtracker;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.group20.dailyreadingtracker.auth.EmailService;
import com.group20.dailyreadingtracker.auth.VerificationToken;
import com.group20.dailyreadingtracker.auth.VerificationTokenRepository;
import com.group20.dailyreadingtracker.auth.VerificationTokenService;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private VerificationTokenRepository tokenRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private VerificationTokenService verificationTokenService;
    
    private User testUser;
    private VerificationToken testToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setIsEnabled(false);
        
        testToken = new VerificationToken();
        testToken.setToken("testToken");
        testToken.setUser(testUser);
        testToken.setStatus(VerificationToken.STATUS_PENDING);
        testToken.setExpiredDateTime(LocalDateTime.now().plusHours(24));
    }

    @Test
    @Transactional
    void testCreateVerificationForRegisteredUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));
        when(request.getServletPath()).thenReturn("");
        
        verificationTokenService.createVerificationForRegisteredUser("test@example.com", request);
        
        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendVerificationEmail(
            eq(testUser),
            startsWith("http://localhost:8080/verify-email?token=")
        );
    }

    @Test
    void testVerifyEmailValidToken() {
        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(testToken));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        ResponseEntity<String> response = verificationTokenService.verifyEmail("validToken");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(testUser.getIsEnabled());
        assertEquals(VerificationToken.STATUS_VERIFIED, testToken.getStatus());
        assertNotNull(testToken.getConfirmedDateTime());
    }

    @Test
    void testVerifyEmailExpiredToken() {
        testToken.setExpiredDateTime(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(testToken));
        
        ResponseEntity<String> response = verificationTokenService.verifyEmail("expiredToken");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Token expired"));
    }

    @Test
    void testVerifyEmailAlreadyVerified() {
        testToken.setStatus(VerificationToken.STATUS_VERIFIED);
        when(tokenRepository.findByToken("verifiedToken")).thenReturn(Optional.of(testToken));
        
        ResponseEntity<String> response = verificationTokenService.verifyEmail("verifiedToken");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("already verified"));
    }
}
