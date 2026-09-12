package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.dto.request.CancelRequestDTO;
import br.com.fiap.SuperBicho.dto.request.CheckUpRequestDTO;
import br.com.fiap.SuperBicho.entity.Animal;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.service.AnimalService;
import br.com.fiap.SuperBicho.service.AppointmentService;
import br.com.fiap.SuperBicho.service.CheckUpService;
import br.com.fiap.SuperBicho.service.ClinicService;
import br.com.fiap.SuperBicho.service.GuardianService;
import br.com.fiap.SuperBicho.dto.request.AnimalVaccinationRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AnimalResponseDTO;
import br.com.fiap.SuperBicho.service.VaccinationService;
import java.util.List;
import br.com.fiap.SuperBicho.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/guardian")
@RequiredArgsConstructor
public class GuardianHomeController {

    private final GuardianService guardianService;
    private final AnimalService animalService;
    private final AppointmentService appointmentService;
    private final ClinicService clinicService;
    private final CheckUpService checkUpService;
    private final VeterinarianService veterinarianService;
    private final VaccinationService vaccinationService;

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
        List<AnimalResponseDTO> animals = animalService.findByGuardian(guardian.getId());
        model.addAttribute("animals", animals);
        model.addAttribute("appointments", appointmentService.findByGuardian(guardian.getId()));
        model.addAttribute("checkUps", checkUpService.findByGuardian(guardian.getId()));
        model.addAttribute("vaccineAlerts", vaccinationService.buildAlertsForGuardian(animals));
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

    @GetMapping("/pet/{id}/historico")
    public String petHistory(@PathVariable Integer id, Model model, Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        Animal animal = animalService.findEntityById(id);
        if (!animal.getGuardian().getId().equals(guardian.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you");
        }
        model.addAttribute("animal", animal);
        model.addAttribute("appointments", appointmentService.findByAnimal(id));
        model.addAttribute("checkUps", checkUpService.findByAnimal(id));
        return "historico-animal";
    }

    @ModelAttribute("vaccinationDTO")
    public AnimalVaccinationRequestDTO prepareVaccinationDTO(@PathVariable(required = false) Integer id) {
        AnimalVaccinationRequestDTO dto = new AnimalVaccinationRequestDTO();
        if (id != null) {
            dto.setAnimalId(id);
        }
        return dto;
    }

    @GetMapping("/pet/{id}/vacinas")
    public String vaccinationScreen(@PathVariable Integer id, Model model, Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        Animal animal = animalService.findEntityById(id);
        if (!animal.getGuardian().getId().equals(guardian.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you");
        }
        model.addAttribute("animal", animal);
        model.addAttribute("calendar", vaccinationService.buildCalendar(id));
        model.addAttribute("applications", vaccinationService.findByAnimal(id));
        model.addAttribute("vaccines", vaccinationService.findAllVaccines());
        return "vacinas";
    }

    @PostMapping("/pet/{id}/vacinas/{vaccineId}/aplicar")
    public String marcarVacinaAplicada(@PathVariable Integer id, @PathVariable Integer vaccineId,
                                       Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        Animal animal = animalService.findEntityById(id);
        if (!animal.getGuardian().getId().equals(guardian.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you");
        }
        AnimalVaccinationRequestDTO dto = new AnimalVaccinationRequestDTO();
        dto.setAnimalId(id);
        dto.setVaccineId(vaccineId);
        dto.setApplicationDate(java.time.LocalDate.now().toString());
        vaccinationService.registerApplication(dto);
        return "redirect:/guardian/pet/" + id + "/vacinas";
    }

    @ModelAttribute("appointmentDTO")
    public AppointmentRequestDTO prepareAppointmentDTO() {
        AppointmentRequestDTO dto = new AppointmentRequestDTO();
        dto.setStatus(AppointmentService.STATUS_SCHEDULED);
        return dto;
    }

    @GetMapping("/agendamento")
    public String agendamentoForm(Model model, Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
        model.addAttribute("clinics", clinicService.findApproved());
        model.addAttribute("veterinarians", veterinarianService.findAll());
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

        if (result.hasErrors()) {
            model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
            model.addAttribute("clinics", clinicService.findApproved());
            return "agendamento";
        }
        appointmentService.create(appointmentDTO);
        return "redirect:/guardian/home";
    }

    @PostMapping("/agendamento/{id}/cancelar")
    public String agendamentoCancelar(@PathVariable Integer id,
                                       @Valid @ModelAttribute("cancelDTO") CancelRequestDTO cancelDTO,
                                       BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("targetUrl", "/guardian/agendamento/" + id + "/cancelar");
            model.addAttribute("title", "Cancelar agendamento");
            return "cancelamento";
        }
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        appointmentService.cancelByGuardian(id, guardian.getId(), cancelDTO.getReason());
        return "redirect:/guardian/home";
    }

    @GetMapping("/agendamento/{id}/cancelar")
    public String agendamentoCancelarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("cancelDTO", new CancelRequestDTO());
        model.addAttribute("targetUrl", "/guardian/agendamento/" + id + "/cancelar");
        model.addAttribute("title", "Cancelar agendamento");
        return "cancelamento";
    }

    @ModelAttribute("checkUpDTO")
    public CheckUpRequestDTO prepareCheckUpDTO() {
        CheckUpRequestDTO dto = new CheckUpRequestDTO();
        dto.setStatus(CheckUpService.STATUS_PENDING);
        return dto;
    }

    @GetMapping("/exame")
    public String exameForm(Model model, Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        model.addAttribute("pets", animalService.findByGuardian(guardian.getId()));
        model.addAttribute("clinics", clinicService.findApproved());
        model.addAttribute("veterinarians", veterinarianService.findAll());
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
            model.addAttribute("clinics", clinicService.findApproved());
            model.addAttribute("veterinarians", veterinarianService.findAll());
            return "exame";
        }
        checkUpService.create(checkUpDTO);
        return "redirect:/guardian/home";
    }

    @GetMapping("/exame/{id}/cancelar")
    public String exameCancelarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("cancelDTO", new CancelRequestDTO());
        model.addAttribute("targetUrl", "/guardian/exame/" + id + "/cancelar");
        model.addAttribute("title", "Cancelar exame");
        return "cancelamento";
    }

    @PostMapping("/exame/{id}/cancelar")
    public String exameCancelar(@PathVariable Integer id,
                                 @Valid @ModelAttribute("cancelDTO") CancelRequestDTO cancelDTO,
                                 BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("targetUrl", "/guardian/exame/" + id + "/cancelar");
            model.addAttribute("title", "Cancelar exame");
            return "cancelamento";
        }
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        checkUpService.cancelByGuardian(id, guardian.getId(), cancelDTO.getReason());
        return "redirect:/guardian/home";
    }

    @PostMapping("/pet/{id}/vacinas/{vaccineId}/desmarcar")
    public String desmarcarVacinaAplicada(@PathVariable Integer id, @PathVariable Integer vaccineId,
                                          Authentication authentication) {
        Guardian guardian = guardianService.findByEmail(authentication.getName());
        Animal animal = animalService.findEntityById(id);
        if (!animal.getGuardian().getId().equals(guardian.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you");
        }
        vaccinationService.desfazerAplicacaoHoje(id, vaccineId);
        return "redirect:/guardian/pet/" + id + "/vacinas";
    }
}
