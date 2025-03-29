package com.group20.dailyreadingtracker.auth;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group20.dailyreadingtracker.security.SecurityService;
import com.group20.dailyreadingtracker.user.User;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    
    private final AuthService authService;
    private final SecurityService securityService;
    private final EmailService emailService;
    private final PasswordResetTokenService passwordResetTokenService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, SecurityService securityService,  EmailService emailService, PasswordResetTokenService passwordResetTokenService){
        this.authService = authService;
        this.securityService = securityService;
        this.emailService = emailService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        if(securityService.isAuthenticated())
            return "redirect:/home";

        model.addAttribute("user", new User());
        
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user) {
        authService.register(user);

        securityService.autoLogin(user.getEmail(), user.getPassword());

        return "redirect:/home";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model, String error, String logout) {
        if (securityService.isAuthenticated())
            return "redirect:/home";

        if (error != null)
            model.addAttribute("error", "Your email or password is invalid.");
        
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(){
        if (securityService.isAuthenticated()) {
            return "redirect:/home";
        }
        return "forgot-password";
    }
   
    @RateLimiter(name = "passwordResetLimiter")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestPasswordReset(
            @RequestParam String email, 
            HttpServletRequest servletRequest) {
        
        Optional<User> user = authService.findByEmail(email);
        
        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            passwordResetTokenService.createPasswordResetTokenForUser(user.get(), token);
            
            String resetUrl = authService.generatePasswordResetUrl(user.get(), servletRequest, token);
            
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
}
