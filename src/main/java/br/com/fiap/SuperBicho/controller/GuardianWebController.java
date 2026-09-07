package br.com.fiap.SuperBicho.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuardianWebController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}