package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.service.AnimalService;
import br.com.fiap.SuperBicho.service.GuardianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/guardian")
@RequiredArgsConstructor
public class GuardianHomeController {

    private final GuardianService guardianService;
    private final AnimalService animalService;

    @ModelAttribute("animalDTO")
    public AnimalRequestDTO prepareAnimalDTO(Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        AnimalRequestDTO dto = new AnimalRequestDTO();
        dto.setGuardianId(guardian.getId());
        return dto;
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        model.addAttribute("animals", animalService.findByGuardian(guardian.getId()));
        return "guardian-home";
    }

    @GetMapping("/pet")
    public String petForm() {
        return "cadastro-pet";
    }

    @PostMapping("/pet")
    public String petSubmit(@Valid @ModelAttribute AnimalRequestDTO animalDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "cadastro-pet";
        }
        animalService.create(animalDTO);
        return "redirect:/guardian/home";
    }
}