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
import br.com.fiap.SuperBicho.dto.request.CheckUpRequestDTO;
import br.com.fiap.SuperBicho.service.CheckUpService;

@Controller
@RequestMapping("/guardian")
@RequiredArgsConstructor
public class GuardianHomeController {

    private final GuardianService guardianService;
    private final AnimalService animalService;
    private final AppointmentService appointmentService;
    private final ClinicService clinicService;
    private final CheckUpService checkUpService;

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
        model.addAttribute("appointments", appointmentService.findByGuardian(guardian.getId()));
        model.addAttribute("checkUps", checkUpService.findByGuardian(guardian.getId()));
        return "guardian-home";
    }

    @GetMapping("/pet")
    public String petForm() {
        return "cadastro-pet";
    }

    @PostMapping("/pet")
    public String petSubmit(@Valid @ModelAttribute("animalDTO") AnimalRequestDTO animalDTO, BindingResult result) {
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
    public String agendamentoSubmit(@Valid @ModelAttribute("appointmentDTO") AppointmentRequestDTO appointmentDTO, BindingResult result,
                                    Authentication authentication, Model model) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        boolean petPertenceAoTutor = animalService.findByGuardian(guardian.getId()).stream()
                .anyMatch(pet -> pet.getId().equals(appointmentDTO.getAnimalId()));

        if (!petPertenceAoTutor) {
            result.rejectValue("animalId", "invalid", "Pet inválido");
        }

        System.out.println("=== DEBUG AGENDAMENTO ===");
        System.out.println("guardian logado id=" + guardian.getId() + " email=" + guardian.getEmail());
        System.out.println("animalId enviado=" + appointmentDTO.getAnimalId());
        System.out.println("petPertenceAoTutor=" + petPertenceAoTutor);
        System.out.println("hasErrors=" + result.hasErrors());
        result.getAllErrors().forEach(e -> System.out.println("erro: " + e.getDefaultMessage()));
        System.out.println("=========================");

        if (result.hasErrors()) {
            model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
            model.addAttribute("clinics", clinicService.findApproved());
            return "agendamento";
        }
        appointmentService.create(appointmentDTO);
        return "redirect:/guardian/home";
    }
    @ModelAttribute("checkUpDTO")
    public CheckUpRequestDTO prepareCheckUpDTO() {
        CheckUpRequestDTO dto = new CheckUpRequestDTO();
        dto.setStatus("PENDENTE");
        return dto;
    }
    @GetMapping("/exame")
    public String exameForm(Model model, Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
        return "exame";
    }

    @PostMapping("/exame")
    public String exameSubmit(@Valid @ModelAttribute("checkUpDTO") CheckUpRequestDTO checkUpDTO, BindingResult result,
                              Authentication authentication, Model model) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        boolean petPertenceAoTutor = animalService.findByGuardian(guardian.getId()).stream()
                .anyMatch(pet -> pet.getId().equals(checkUpDTO.getAnimalId()));

        if (!petPertenceAoTutor) {
            result.rejectValue("animalId", "invalid", "Pet inválido");
        }
        if (result.hasErrors()) {
            model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
            return "exame";
        }
        checkUpService.create(checkUpDTO);
        return "redirect:/guardian/home";
    }

}