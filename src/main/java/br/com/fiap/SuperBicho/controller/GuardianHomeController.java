package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.service.AnimalService;
import br.com.fiap.SuperBicho.service.AppointmentService;
import br.com.fiap.SuperBicho.service.ClinicService;
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
    private final AppointmentService appointmentService;
    private final ClinicService clinicService;

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

    @ModelAttribute("appointmentDTO")
    public AppointmentRequestDTO prepareAppointmentDTO() {
        AppointmentRequestDTO dto = new AppointmentRequestDTO();
        dto.setStatus("AGENDADO");
        return dto;
    }

    @GetMapping("/agendamento")
    public String agendamentoForm(Model model, Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
        model.addAttribute("clinics", clinicService.findApproved());
        return "agendamento";
    }

    @PostMapping("/agendamento")
    public String agendamentoSubmit(@Valid @ModelAttribute AppointmentRequestDTO appointmentDTO, BindingResult result,
                                    Authentication authentication, Model model) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        boolean petPertenceAoTutor = animalService.findByGuardian(guardian.getId()).stream()
                .anyMatch(pet -> pet.getId().equals(appointmentDTO.getAnimalId()));

        if (!petPertenceAoTutor) {
            result.rejectValue("animalId", "invalid", "Pet inválido");
        }
        if (result.hasErrors()) {
            model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
            model.addAttribute("clinics", clinicService.findApproved());
            return "agendamento";
        }
        appointmentService.create(appointmentDTO);
        return "redirect:/guardian/home";
    }
}