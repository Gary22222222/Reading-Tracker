package com.group20.dailyreadingtracker;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.group20.dailyreadingtracker.auth.PasswordResetToken;
import com.group20.dailyreadingtracker.auth.PasswordResetTokenRepository;
import com.group20.dailyreadingtracker.auth.PasswordResetTokenService;
import com.group20.dailyreadingtracker.user.User;

public class PasswordResetTokenServiceTest {
    
    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @InjectMocks
    private PasswordResetTokenService tokenService;

    @Test
    public void testCreateToken(){
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("Password123");

        tokenService.createPasswordResetTokenForUser(user, "test-token");

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        
        assertEquals(user, tokenCaptor.getValue().getUser());
        assertEquals("test-token", tokenCaptor.getValue().getToken());
    }

    @Test
    public void testValidateCorrectToken(){
        PasswordResetToken validToken = new PasswordResetToken("valid-token", new User());
        validToken.setExpirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
        
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));
        
        assertEquals("valid", tokenService.validatePasswordResetToken("valid-token"));
    }

    @Test
    public void testValidateExpiredToken(){
        PasswordResetToken expiredToken = new PasswordResetToken("expired-token", new User());
        expiredToken.setExpirationTime(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));
        
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));
        
        assertEquals("Link already expired, resend link", 
            tokenService.validatePasswordResetToken("expired-token"));
    }
}
