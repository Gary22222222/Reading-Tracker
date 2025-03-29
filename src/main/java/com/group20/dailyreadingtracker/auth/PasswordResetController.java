package com.group20.dailyreadingtracker.auth;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;


@Controller
public class PasswordResetController {

    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final PasswordResetService passwordResetService;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    public PasswordResetController(PasswordResetTokenRepository tokenRepository, PasswordResetTokenService passwordResetTokenService, PasswordResetService passwordResetService){
        this.tokenRepository = tokenRepository;
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam String token, Model model) {
        try {
            String validationResult = passwordResetTokenService.validatePasswordResetToken(token);
            
            if (!"valid".equals(validationResult)){
                logger.warn("Invalid password reset token: {}", token);
                model.addAttribute("error", validationResult);
                return "error";
            }

            model.addAttribute("token", token);
            return "resetPasswordForm";
        } catch (Exception e) {
            logger.error("Error processing password reset request");
            model.addAttribute("error", "An unexpected error occurred");
            return "error";
        }
        
    }

    @RateLimiter(name = "passwordResetAttemptLimiter")
    @PostMapping("/reset-password")
    public String processPasswordReset(@RequestParam String token, @RequestParam String newPassword, @RequestParam String confirmPassword, RedirectAttributes redirectAttrs) {

        // Password match validation
        if (!newPassword.equals(confirmPassword)) {
            redirectAttrs.addAttribute("error", "Passwords don't match");
            return "redirect:/reset-password?token=" + token;
        }

        // Token validation
        String validationResult = passwordResetTokenService.validatePasswordResetToken(token);
        if (!"valid".equals(validationResult)) {
            redirectAttrs.addAttribute("error", validationResult);
            return "redirect:/reset-password?token=" + token;
        }

        // Get and validate token
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        if (!tokenOptional.isPresent() || tokenOptional.get().isExpired()) {
            redirectAttrs.addAttribute("error", "Invalid or expired token");
            return "redirect:/reset-password?token=" + token;
        }

        PasswordResetToken resetToken = tokenOptional.get();
        passwordResetService.resetPassword(resetToken.getUser(), newPassword);
        tokenRepository.delete(resetToken);

        redirectAttrs.addFlashAttribute("success", "Password reset successfully");
        return "redirect:/login";
    }
    
    
}
