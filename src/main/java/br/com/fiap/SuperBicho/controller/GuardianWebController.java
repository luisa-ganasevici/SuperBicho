package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.service.GuardianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class GuardianWebController {

    private final GuardianService guardianService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("guardian", new Guardian());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastroSubmit(@Valid @ModelAttribute Guardian guardian, BindingResult result) {
        if (result.hasErrors()) {
            return "cadastro";
        }
        guardianService.create(guardian);
        return "redirect:/login";
    }
}