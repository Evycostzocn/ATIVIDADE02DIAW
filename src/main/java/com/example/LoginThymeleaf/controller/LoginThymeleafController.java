package com.example.LoginThymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginThymeleafController {
    
    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
