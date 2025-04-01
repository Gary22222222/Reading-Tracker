package com.group20.dailyreadingtracker.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.group20.dailyreadingtracker.security.SecurityService;
import com.group20.dailyreadingtracker.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {
    
    private final AuthService authService;
    private final SecurityService securityService;

    public AuthController(AuthService authService, SecurityService securityService, VerificationTokenService verificationTokenService){
        this.authService = authService;
        this.securityService = securityService;
    }

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        if(securityService.isAuthenticated())
            return "redirect:/home";

        model.addAttribute("user", new User());
        
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user, @RequestParam(required = false) MultipartFile avatar, HttpServletRequest request) {
        authService.register(user, avatar, request);
        return "redirect:/verify-pending?email=" + user.getEmail();
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout=true";
    }
   
}
