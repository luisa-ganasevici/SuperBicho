package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.service.ClinicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/clinics") @RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;

    @GetMapping
    public Page<Clinic> findAll(Pageable pageable) {
        return clinicService.findAll(pageable); }

    @GetMapping("/{id}")
    public Clinic findById(@PathVariable Integer id) {
        return clinicService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) public Clinic create(@Valid @RequestBody Clinic clinic) {
        return clinicService.create(clinic); }

    @PutMapping("/{id}")
    public Clinic update(@PathVariable Integer id, @Valid @RequestBody Clinic clinic) {
        return clinicService.update(id, clinic); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteById(@PathVariable Integer id) {
        clinicService.deleteById(id); }
}
