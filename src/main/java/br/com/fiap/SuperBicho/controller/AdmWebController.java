package br.com.fiap.SuperBicho.controller;


import br.com.fiap.SuperBicho.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
@RequiredArgsConstructor
public class AdmWebController {

    private final ClinicService clinicService;

    @GetMapping("/login")
    public String login() {
        return "adm-login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("clinics", clinicService.findPending());
        return "adm-home";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Integer id) {
        clinicService.approve(id);
        return "redirect:/adm/home";
    }

    @PostMapping("/deny/{id}")
    public String deny(@PathVariable Integer id) {
        clinicService.deny(id);
        return "redirect:/adm/home";
    }
}