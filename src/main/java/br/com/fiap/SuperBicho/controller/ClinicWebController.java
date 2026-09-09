package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.service.ClinicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clinica")
public class ClinicWebController {

    private final ClinicService clinicService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/login")
    public String login() {
        return "clinic-login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("clinic", new Clinic());
        return "clinic-cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastroSubmit(@Valid @ModelAttribute Clinic clinic, BindingResult result) {
        if (result.hasErrors()) {
            return "clinic-cadastro";
        }
        clinicService.create(clinic);
        return "redirect:/clinica/login";
    }
}