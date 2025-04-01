package com.group20.dailyreadingtracker.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class VerificationController {
    private final VerificationTokenService verificationTokenService;

    public VerificationController(VerificationTokenService verificationTokenService){
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, Model model) {
        try {
            verificationTokenService.verifyEmail(token);
            return "redirect:/login?verified=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "verification-error";
        }
    }

    @GetMapping("/verify-pending")
    public String showVerifyPending(@RequestParam String email, @RequestParam(required = false) Boolean resent, Model model) {
        model.addAttribute("email", email);
        if (Boolean.TRUE.equals(resent)) {
            model.addAttribute("message", "New verification email sent!");
        }
        return "verify-pending";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam String email, HttpServletRequest request) {
        verificationTokenService.createVerificationForRegisteredUser(email, request);
        return "redirect:/verify-pending?email=" + email + "&resent=true";
    }
}
