package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AnimalResponseDTO;
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
    public Page<AnimalResponseDTO> findAll(Pageable pageable) {
        return animalService.findAll(pageable); }

    @GetMapping("/species")
    public Page<AnimalResponseDTO> findBySpecies(@RequestParam String species, Pageable pageable) {
        return animalService.findBySpecies(species, pageable); }

    @GetMapping("/guardian/{guardianId}")
    public List<AnimalResponseDTO> findByGuardian(@PathVariable Integer guardianId) {
        return animalService.findByGuardian(guardianId); }

    @GetMapping("/{id}")
    public AnimalResponseDTO findById(@PathVariable Integer id) {
        return animalService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponseDTO create(@Valid @RequestBody AnimalRequestDTO dto) {
        return animalService.create(dto); }

    @PutMapping("/{id}")
    public AnimalResponseDTO update(@PathVariable Integer id, @Valid @RequestBody AnimalRequestDTO dto) {
        return animalService.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) { animalService.deleteById(id); }
}