package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.GuardianRequestDTO;
import br.com.fiap.SuperBicho.dto.response.GuardianResponseDTO;
import br.com.fiap.SuperBicho.service.GuardianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/guardians") @RequiredArgsConstructor
public class GuardianController {

    private final GuardianService guardianService;

    @GetMapping
    public Page<GuardianResponseDTO> findAll(Pageable pageable) {
        return guardianService.findAll(pageable); }

    @GetMapping("/{id}")
    public GuardianResponseDTO findById(@PathVariable Integer id) {
        return guardianService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuardianResponseDTO create(@Valid @RequestBody GuardianRequestDTO dto) {
        return guardianService.create(dto); }

    @PutMapping("/{id}")
    public GuardianResponseDTO update(@PathVariable Integer id, @Valid @RequestBody GuardianRequestDTO dto) {
        return guardianService.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        guardianService.deleteById(id); }
}