package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.AnimalDTO;
import br.com.fiap.SuperBicho.entity.Animal;
import br.com.fiap.SuperBicho.service.AnimalService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/animals") @RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;

    @GetMapping
    public Page <Animal> findAll(Pageable pageable) {
        return animalService.findAll(pageable); }

    @GetMapping("/species")
    public List<Animal> findBySpecies(@RequestParam String species) {
        return animalService.findBySpecies(species); }

    @GetMapping("/guardian/{guardianId}")
    public List<Animal> findByGuardian(@PathVariable Integer guardianId) {
        return animalService.findByGuardian(guardianId); }

    @GetMapping("/{id}")
    public Animal findById(@PathVariable Integer id) {
        return animalService.findById(id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Animal create(@Valid @RequestBody AnimalDTO dto) {
        return animalService.create(dto); }

    @PutMapping("/{id}")
    public Animal update(@PathVariable Integer id, @Valid @RequestBody AnimalDTO dto) {
        return animalService.update(id, dto); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) { animalService.deleteById(id); }
}
