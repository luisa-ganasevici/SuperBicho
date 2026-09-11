package br.com.fiap.SuperBicho.controller;


import br.com.fiap.SuperBicho.service.AppointmentService;
import br.com.fiap.SuperBicho.service.CheckUpService;
import br.com.fiap.SuperBicho.service.ClinicService;
import br.com.fiap.SuperBicho.service.VeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    private static final String MOTIVO_CANCELAMENTO_CLINICA =
            "Consulta cancelada, favor entrar em contato com a SuperBicho para retirar as dúvidas.";

    private final ClinicService clinicService;
    private final VeterinarianService veterinarianService;
    private final AppointmentService appointmentService;
    private final CheckUpService checkUpService;

    @GetMapping("/login")
    public String login() {
        return "adm-login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("clinics", clinicService.findPending());
        model.addAttribute("allClinics", clinicService.findAll(Pageable.unpaged()).getContent());
        model.addAttribute("veterinarians", veterinarianService.findAll());
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

    @PostMapping("/clinica/{id}/excluir")
    public String excluirClinica(@PathVariable Integer id) {
        appointmentService.cancelAllByClinic(id, MOTIVO_CANCELAMENTO_CLINICA);
        checkUpService.cancelAllByClinic(id, MOTIVO_CANCELAMENTO_CLINICA);
        clinicService.disable(id);
        return "redirect:/adm/home";
    }

    @PostMapping("/veterinario/{id}/excluir")
    public String excluirVeterinario(@PathVariable Integer id) {
        veterinarianService.deleteByAdmin(id);
        return "redirect:/adm/home";
    }
}