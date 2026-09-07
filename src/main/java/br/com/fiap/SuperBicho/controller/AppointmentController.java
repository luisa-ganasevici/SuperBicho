package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.Appointment;
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
    public Page<Appointment> findAll(Pageable pageable) {
        return appointmentService.findAll(pageable); }

    @GetMapping("/{id}")
    public Appointment findById(@PathVariable Integer id) {
        return appointmentService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) public Appointment create(@Valid @RequestBody Appointment appointment) {
        return appointmentService.create(appointment); }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable Integer id, @Valid @RequestBody Appointment appointment) {
        return appointmentService.update(id, appointment); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteById(@PathVariable Integer id) {
        appointmentService.deleteById(id); }
}
