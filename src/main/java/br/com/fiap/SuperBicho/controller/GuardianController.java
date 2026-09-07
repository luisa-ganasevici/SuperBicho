package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.Guardian;
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
    public Page<Guardian> findAll(Pageable pageable) {
        return guardianService.findAll(pageable); }

    @GetMapping("/{id}")
    public Guardian findById(@PathVariable Integer id) {
        return guardianService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) public Guardian create(@Valid @RequestBody Guardian guardian) {
        return guardianService.create(guardian); }

    @PutMapping("/{id}")
    public Guardian update(@PathVariable Integer id, @Valid @RequestBody Guardian guardian) {
        return guardianService.update(id, guardian); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteById(@PathVariable Integer id)
    { guardianService.deleteById(id); }
}
