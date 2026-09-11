package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.entity.ClinicStatus;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute(
                "allClinics",
                clinicService.findAll(Pageable.unpaged()).getContent()
        );
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
    public String excluirClinica(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        Clinic clinic = clinicService.findEntityById(id);

        if (clinic.getClinicStatus() == ClinicStatus.REMOVED) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Esta clínica já foi removida e não pode ser excluída novamente."
            );
            return "redirect:/adm/home";
        }

        if (clinic.getCnpj() == null || clinic.getCnpj().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível remover a clínica: o CNPJ não está cadastrado."
            );
            return "redirect:/adm/home";
        }

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