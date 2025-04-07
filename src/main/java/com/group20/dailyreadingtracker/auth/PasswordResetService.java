package com.group20.dailyreadingtracker.auth;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

// Manages password reset token generation, validation and password updates

@Service
public class PasswordResetService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    public PasswordResetService(PasswordEncoder encoder, UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService){
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }
    
    public void resetPassword(User user, String newPassword){
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
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
        
        if (!tokenOptional.isPresent()) {
            return "Invalid password reset token";
        }
        
        PasswordResetToken token = tokenOptional.get();
        
        if (token.isExpired()) {
            return "Link already expired, resend link";
        }
        
        return "valid";
    }

    public String processPasswordReset(String token, String newPassword, String confirmPassword, RedirectAttributes redirectAttributes){
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addAttribute("error", "Passwords don't match");
            return "redirect:/reset-password?token=" + token;
        }

        String validationResult = validatePasswordResetToken(token);
        if (!"valid".equals(validationResult)) {
            redirectAttributes.addAttribute("error", validationResult);
            return "redirect:/reset-password?token=" + token;
        }

        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(token);
        if (!tokenOptional.isPresent() || tokenOptional.get().isExpired()) {
            redirectAttributes.addAttribute("error", "Invalid or expired token");
            return "redirect:/reset-password?token=" + token;
        }

        PasswordResetToken resetToken = tokenOptional.get();
        resetPassword(resetToken.getUser(), newPassword);
        passwordResetTokenRepository.delete(resetToken);

        redirectAttributes.addFlashAttribute("success", "Password reset successfully");
        return "redirect:/auth";
    }

    public String requestPasswordReset(String email, HttpServletRequest request) {
        Optional<User> user = userRepository.findByEmail(email);
        
        if (user.isPresent()) {
            try {
                String token = UUID.randomUUID().toString();
                createPasswordResetTokenForUser(user.get(), token);
                String resetUrl = generatePasswordResetUrl(request, token);
                emailService.sendPasswordResetEmail(user.get(), resetUrl);
                return "If this email exists, a reset link has been sent";
            } catch (Exception e) {
                logger.error("Failed to send password reset email", e);
                return "Error: Failed to send reset email. Please try again later.";
            }
        }
        return "If this email exists, a reset link has been sent";
    }
    
    public void invalidateExistingTokens(String email) {
    passwordResetTokenRepository.findByUserEmail(email)
        .ifPresent(token -> {
            token.setExpirationTime(LocalDateTime.now()); 
            passwordResetTokenRepository.save(token);
        });
    }

    @Transactional
    public Optional<User> findUserByPasswordToken(String passwordToken) {
        Optional<PasswordResetToken> token = passwordResetTokenRepository.findByToken(passwordToken);
        return token.isPresent() ? Optional.of(token.get().getUser()) : Optional.empty();
    }

    private String generatePasswordResetUrl(HttpServletRequest request, String token) {
        String baseUrl = request.getRequestURL().toString()
                .replace(request.getServletPath(), "");
        return baseUrl + "/reset-password?token=" + token;
    }

}
