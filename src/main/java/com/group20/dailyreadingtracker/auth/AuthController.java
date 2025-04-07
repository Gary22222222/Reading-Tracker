package com.group20.dailyreadingtracker.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group20.dailyreadingtracker.security.SecurityService;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    
    private final AuthService authService;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, SecurityService securityService, UserRepository userRepository){
        this.authService = authService;
        this.securityService = securityService;
        this.userRepository = userRepository;
    }

    @GetMapping("/auth")
    public String getAuthPage(Model model,
                            @RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String mode) {
        if (securityService.isAuthenticated())
            return "redirect:/home";

        model.addAttribute("user", new User());
        
        if (error != null)
            model.addAttribute("error", "Your email or password is invalid.");
        
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        
        if ("register".equals(mode))
            model.addAttribute("defaultToRegister", true);

        return "authentication";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam(required = false) MultipartFile avatar, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty())
            result.rejectValue("confirmPassword", "NotEmpty", "Password confirmation is required");

        if (!user.isPasswordsMatch())
            result.rejectValue("confirmPassword", "Match", "Passwords must match");

        if (userRepository.existsByEmail(user.getEmail()))
            result.rejectValue("email", "Duplicate", "Taken");

        if (userRepository.existsByUsername(user.getUsername()))
            result.rejectValue("username", "Duplicate", "Taken");

        if (result.hasErrors()) {
            model.addAttribute("defaultToRegister", true);
            model.addAttribute("showRegisterForm", true);
            model.addAttribute("containerClass", "sign-up-mode");
            return "authentication";
        }
        
        authService.register(user, avatar, request);
        return "redirect:/verify-pending?email=" + user.getEmail();
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, 
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes) {
        try {
            securityService.autoLogin(username, password);
            return "redirect:/home";
        } catch (SecurityService.EmailNotVerifiedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("unverifiedEmail", username);
            return "redirect:/auth?mode=login";
        } catch (BadCredentialsException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/auth?mode=login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth?logout=true";
    }
   
}
