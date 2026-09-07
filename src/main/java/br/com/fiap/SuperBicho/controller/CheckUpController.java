package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.CheckUp;
import br.com.fiap.SuperBicho.service.CheckUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/check-ups") @RequiredArgsConstructor
public class CheckUpController {

    private final CheckUpService checkUpService;

    @GetMapping
    public Page<CheckUp> findAll(Pageable pageable) {
        return checkUpService.findAll(pageable); }

    @GetMapping("/{id}")
    public CheckUp findById(@PathVariable Integer id) {
        return checkUpService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) public CheckUp create(@Valid @RequestBody CheckUp checkUp) {
        return checkUpService.create(checkUp); }

    @PutMapping("/{id}")
    public CheckUp update(@PathVariable Integer id, @Valid @RequestBody CheckUp checkUp) {
        return checkUpService.update(id, checkUp); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteById(@PathVariable Integer id) {
        checkUpService.deleteById(id); }
}
