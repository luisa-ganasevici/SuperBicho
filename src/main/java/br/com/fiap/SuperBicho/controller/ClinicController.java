package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.ClinicRequestDTO;
import br.com.fiap.SuperBicho.dto.response.ClinicResponseDTO;
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
    public Page<ClinicResponseDTO> findAll(Pageable pageable) {
        return clinicService.findAll(pageable); }

    @GetMapping("/{id}")
    public ClinicResponseDTO findById(@PathVariable Integer id) {
        return clinicService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicResponseDTO create(@Valid @RequestBody ClinicRequestDTO dto) {
        return clinicService.create(dto); }

    @PutMapping("/{id}")
    public ClinicResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ClinicRequestDTO dto) {
        return clinicService.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        clinicService.deleteById(id); }
}