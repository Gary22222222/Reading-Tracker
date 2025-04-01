package com.group20.dailyreadingtracker.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group20.dailyreadingtracker.security.SecurityService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    private final SecurityService securityService;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    public PasswordResetController(PasswordResetService passwordResetService, SecurityService securityService){
        this.passwordResetService = passwordResetService;
        this.securityService = securityService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(){
        if (securityService.isAuthenticated()) {
            return "redirect:/home";
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam String token, Model model) {
        try {
            String validationResult = passwordResetService.validatePasswordResetToken(token);
            
            if (!"valid".equals(validationResult)){
                logger.warn("Invalid password reset token: {}", token);
                model.addAttribute("error", validationResult);
                return "error";
            }

            PasswordResetRequest request = new PasswordResetRequest();
            request.setToken(token);
            model.addAttribute("passwordResetRequest", request);
            
            return "resetPasswordForm";
        } catch (Exception e) {
            logger.error("Error processing password reset request");
            model.addAttribute("error", "An unexpected error occurred");
            return "error";
        }
    }

    @RateLimiter(name = "passwordResetAttemptLimiter")
    @PostMapping("/reset-password")
    public String processPasswordReset(@Valid PasswordResetRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                redirectAttributes.addFlashAttribute("error", error.getDefaultMessage());
            });

            return "redirect:/reset-password?token=" + request.getToken();
        }

        return passwordResetService.processPasswordReset(
            request.getToken(), 
            request.getNewPassword(), 
            request.getConfirmPassword(), 
            redirectAttributes
        );
    }
    
    @RateLimiter(name = "passwordResetLimiter")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email, 
                                                     HttpServletRequest servletRequest) {
        return passwordResetService.requestPasswordReset(email, servletRequest);
    }
}
