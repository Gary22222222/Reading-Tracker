package com.group20.dailyreadingtracker.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.group20.dailyreadingtracker.security.SecurityService;
import com.group20.dailyreadingtracker.user.User;

@Controller
public class AuthController {
    
    @Autowired
    private AuthService AuthService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        if(securityService.isAuthenticated())
            return "redirect:/home";

        model.addAttribute("user", new User());
        
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user) {
        AuthService.register(user);

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

    @GetMapping("/forgotPassword")
    public String getForgotPasswordForm(){
        return "forgotPassword";
    }

}
