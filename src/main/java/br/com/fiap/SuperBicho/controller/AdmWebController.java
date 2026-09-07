package br.com.fiap.SuperBicho.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
public class AdmWebController {

    @GetMapping("/login")
    public String login() {
        return "adm-login";
    }
}
