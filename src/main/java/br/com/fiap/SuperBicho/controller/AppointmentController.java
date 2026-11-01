package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.dto.request.CancelRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AppointmentResponseDTO;
import br.com.fiap.SuperBicho.service.AppointmentService;
import br.com.fiap.SuperBicho.service.GuardianService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/appointments") @RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final GuardianService guardianService;

    @GetMapping
    public List<AppointmentResponseDTO> findMine(Authentication authentication) {
        return appointmentService.findByGuardian(guardianService.findIdByEmail(authentication.getName())); }

    @GetMapping("/{id}")
    public AppointmentResponseDTO findById(@PathVariable Integer id, Authentication authentication) {
        return appointmentService.findByIdForGuardian(id, guardianService.findIdByEmail(authentication.getName())); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDTO create(@Valid @RequestBody AppointmentRequestDTO dto, Authentication authentication) {
        return appointmentService.create(guardianService.findIdByEmail(authentication.getName()), dto); }

    @PutMapping("/{id}")
    public AppointmentResponseDTO update(@PathVariable Integer id, @Valid @RequestBody AppointmentRequestDTO dto, Authentication authentication) {
        return appointmentService.update(id, guardianService.findIdByEmail(authentication.getName()), dto); }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponseDTO cancel(@PathVariable Integer id, @Valid @RequestBody CancelRequestDTO dto, Authentication authentication) {
        return appointmentService.cancelByGuardian(id, guardianService.findIdByEmail(authentication.getName()), dto.getReason()); }
}
