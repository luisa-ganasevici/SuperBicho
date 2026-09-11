package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.CancelRequestDTO;
import br.com.fiap.SuperBicho.dto.request.VeterinarianRequestDTO;
import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.service.AppointmentService;
import br.com.fiap.SuperBicho.service.CheckUpService;
import br.com.fiap.SuperBicho.service.ClinicService;
import br.com.fiap.SuperBicho.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clinica")
public class ClinicWebController {

    private final ClinicService clinicService;
    private final AppointmentService appointmentService;
    private final CheckUpService checkUpService;
    private final VeterinarianService veterinarianService;

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

    @GetMapping("/status")
    public String status(Model model, Authentication authentication) {
        Clinic clinic = clinicService.findByEmail(authentication.getName());
        model.addAttribute("clinic", clinic);
        return "clinic-status";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        Clinic clinic = clinicService.findByEmail(authentication.getName());
        model.addAttribute("clinic", clinic);
        model.addAttribute("appointments", appointmentService.findByClinic(clinic.getId()));
        model.addAttribute("checkUps", checkUpService.findByClinic(clinic.getId()));
        return "clinic-home";
    }

    @PostMapping("/agendamento/{id}/concluir")
    public String concluirAgendamento(@PathVariable Integer id, Authentication authentication) {
        Clinic clinic = clinicService.findByEmail(authentication.getName());
        appointmentService.completeByClinic(id, clinic.getId());
        return "redirect:/clinica/home";
    }

    @PostMapping("/exame/{id}/concluir")
    public String concluirExame(@PathVariable Integer id, Authentication authentication) {
        Clinic clinic = clinicService.findByEmail(authentication.getName());
        checkUpService.completeByClinic(id, clinic.getId());
        return "redirect:/clinica/home";
    }

    @GetMapping("/agendamento/{id}/cancelar")
    public String cancelarAgendamentoForm(@PathVariable Integer id, Model model) {
        model.addAttribute("cancelDTO", new CancelRequestDTO());
        model.addAttribute("targetUrl", "/clinica/agendamento/" + id + "/cancelar");
        model.addAttribute("title", "Cancelar agendamento");
        return "cancelamento";
    }

    @PostMapping("/agendamento/{id}/cancelar")
    public String cancelarAgendamento(@PathVariable Integer id,
                                      @Valid @ModelAttribute("cancelDTO") CancelRequestDTO cancelDTO,
                                      BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("targetUrl", "/clinica/agendamento/" + id + "/cancelar");
            model.addAttribute("title", "Cancelar agendamento");
            return "cancelamento";
        }
        Clinic clinic = clinicService.findByEmail(authentication.getName());
        appointmentService.cancelByClinic(id, clinic.getId(), cancelDTO.getReason());
        return "redirect:/clinica/home";
    }

    @ModelAttribute("veterinarianDTO")
    public VeterinarianRequestDTO prepareVeterinarianDTO(Authentication authentication) {
        VeterinarianRequestDTO dto = new VeterinarianRequestDTO();
        boolean autenticado = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        if (autenticado) {
            Clinic clinic = clinicService.findByEmail(authentication.getName());
            if (clinic != null) {
                dto.setClinicId(clinic.getId());
            }
        }
        return dto;
    }

    @GetMapping("/veterinarios")
    public String veterinarios(Model model, Authentication authentication) {
        Clinic clinic = clinicService.findByEmail(authentication.getName());
        model.addAttribute("clinic", clinic);
        model.addAttribute("veterinarians", veterinarianService.findByClinic(clinic.getId()));
        return "clinic-veterinarios";
    }

    @PostMapping("/veterinarios")
    public String veterinariosSubmit(@Valid @ModelAttribute("veterinarianDTO") VeterinarianRequestDTO veterinarianDTO,
                                     BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            Clinic clinic = clinicService.findByEmail(authentication.getName());
            model.addAttribute("clinic", clinic);
            model.addAttribute("veterinarians", veterinarianService.findByClinic(clinic.getId()));
            return "clinic-veterinarios";
        }
        veterinarianService.create(veterinarianDTO);
        return "redirect:/clinica/veterinarios";
    }

}