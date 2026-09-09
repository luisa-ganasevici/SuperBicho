package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AppointmentResponseDTO;
import br.com.fiap.SuperBicho.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/appointments") @RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public Page<AppointmentResponseDTO> findAll(Pageable pageable) {
        return appointmentService.findAll(pageable); }

    @GetMapping("/{id}")
    public AppointmentResponseDTO findById(@PathVariable Integer id) {
        return appointmentService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDTO create(@Valid @RequestBody AppointmentRequestDTO dto) {
        return appointmentService.create(dto); }

    @PutMapping("/{id}")
    public AppointmentResponseDTO update(@PathVariable Integer id, @Valid @RequestBody AppointmentRequestDTO dto) {
        return appointmentService.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        appointmentService.deleteById(id); }
}