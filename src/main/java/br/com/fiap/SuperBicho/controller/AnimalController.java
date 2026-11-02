package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AnimalResponseDTO;
import br.com.fiap.SuperBicho.service.AnimalRemovalService;
import br.com.fiap.SuperBicho.service.AnimalService;
import br.com.fiap.SuperBicho.service.GuardianService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/animals") @RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;
    private final AnimalRemovalService animalRemovalService;
    private final GuardianService guardianService;

    @GetMapping
    public List<AnimalResponseDTO> findMine(Authentication authentication) {
        return animalService.findByGuardian(guardianService.findIdByEmail(authentication.getName())); }

    @GetMapping("/{id}")
    public AnimalResponseDTO findById(@PathVariable Integer id, Authentication authentication) {
        return animalService.findByIdForGuardian(id, guardianService.findIdByEmail(authentication.getName())); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponseDTO create(@Valid @RequestBody AnimalRequestDTO dto, Authentication authentication) {
        return animalService.create(guardianService.findIdByEmail(authentication.getName()), dto); }

    @PutMapping("/{id}")
    public AnimalResponseDTO update(@PathVariable Integer id, @Valid @RequestBody AnimalRequestDTO dto, Authentication authentication) {
        return animalService.update(id, guardianService.findIdByEmail(authentication.getName()), dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id, Authentication authentication) {
        animalRemovalService.remove(id, guardianService.findIdByEmail(authentication.getName())); }
}
