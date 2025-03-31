package com.group20.dailyreadingtracker.auth;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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

    public String processPasswordReset(String token, String newPassword, String confirmPassword, RedirectAttributes redirectAttributes){
        // Password match validation
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addAttribute("error", "Passwords don't match");
            return "redirect:/reset-password?token=" + token;
        }

        // Token validation
        String validationResult = validatePasswordResetToken(token);
        if (!"valid".equals(validationResult)) {
            redirectAttributes.addAttribute("error", validationResult);
            return "redirect:/reset-password?token=" + token;
        }

        // Get and validate token
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(token);
        if (!tokenOptional.isPresent() || tokenOptional.get().isExpired()) {
            redirectAttributes.addAttribute("error", "Invalid or expired token");
            return "redirect:/reset-password?token=" + token;
        }

        PasswordResetToken resetToken = tokenOptional.get();
        resetPassword(resetToken.getUser(), newPassword);
        passwordResetTokenRepository.delete(resetToken);

        redirectAttributes.addFlashAttribute("success", "Password reset successfully");
        return "redirect:/login";
    }

    public ResponseEntity<String> requestPasswordReset(@RequestParam String email, HttpServletRequest request) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user.get(), token);
            
            String resetUrl = generatePasswordResetUrl(request, token);

            try {
                emailService.sendPasswordResetEmail(user.get(), resetUrl);
                return ResponseEntity.ok("Password reset link sent to your email");
            } catch (Exception e) {
                logger.error("Failed to send password reset email", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send reset email. Please try again later.");
            }
        }
        
        return ResponseEntity.ok("If the email exists, a reset link has been sent");
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
