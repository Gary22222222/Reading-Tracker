package com.group20.dailyreadingtracker.app;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AppController {

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model, HttpServletRequest request) {
        // 从请求中获取 CSRF 令牌并添加到模型中
        model.addAttribute("_csrf", request.getAttribute("_csrf"));
        return "home";
    }
}