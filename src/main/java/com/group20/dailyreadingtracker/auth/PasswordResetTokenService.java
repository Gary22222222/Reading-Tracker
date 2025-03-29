package com.group20.dailyreadingtracker.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group20.dailyreadingtracker.user.User;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository){
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Transactional
    public void createPasswordResetTokenForUser(User user, String passwordToken) {
        PasswordResetToken token = passwordResetTokenRepository.findByUser(user)
            .orElse(new PasswordResetToken(passwordToken, user));
        
        token.setToken(passwordToken);
        token.setExpirationTime(token.getTokenExpirationTime());
        passwordResetTokenRepository.save(token);
    }

    public String validatePasswordResetToken(String tokenValue) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(tokenValue);
        
        // Check if token exists
        if (!tokenOptional.isPresent()) {
            return "Invalid password reset token";
        }
        
        // Get the actual token from Optional
        PasswordResetToken token = tokenOptional.get();
        
        // Check expiration
        if (token.isExpired()) {
            return "Link already expired, resend link";
        }
        
        return "valid";
    }

    @Transactional
    public Optional<User> findUserByPasswordToken(String passwordToken) {
        Optional<PasswordResetToken> token = passwordResetTokenRepository.findByToken(passwordToken);
        return token.isPresent() ? Optional.of(token.get().getUser()) : Optional.empty();
    }
    
}
