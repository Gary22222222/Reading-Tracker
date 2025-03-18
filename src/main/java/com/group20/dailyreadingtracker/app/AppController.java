package com.group20.dailyreadingtracker.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AppController {

    @GetMapping("/home")
    public String getHomePage(){
        return "home";
    }
    
}
