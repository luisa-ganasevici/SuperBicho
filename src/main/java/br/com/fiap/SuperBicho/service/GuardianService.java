package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.GuardianRequestDTO;
import br.com.fiap.SuperBicho.dto.response.GuardianResponseDTO;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable("guardians")
    public Page<GuardianResponseDTO> findAll(Pageable pageable) {
        return guardianRepository.findAll(pageable).map(this::toResponse); }

    @Cacheable(value = "guardianById", key = "#id")
    public GuardianResponseDTO findById(Integer id) {
        return toResponse(findEntityById(id)); }

    public Guardian findEntityById(Integer id) {
        return guardianRepository.findById(id).orElseThrow(() -> notFound()); }

    public Guardian findByEmail(String email) {
        return guardianRepository.findByEmail(email).orElseThrow(() -> notFound()); }

    @CacheEvict(value = {"guardians", "guardianById", "animals", "animalById"}, allEntries = true)
    public GuardianResponseDTO create(GuardianRequestDTO dto) {
        Guardian guardian = new Guardian();
        guardian.setName(dto.getName());
        guardian.setEmail(dto.getEmail());
        guardian.setPassword(passwordEncoder.encode(dto.getPassword()));
        return toResponse(guardianRepository.save(guardian));
    }

    @CacheEvict(value = {"guardians", "guardianById", "animals", "animalById"}, allEntries = true)
    public Guardian create(Guardian guardian) {
        guardian.setPassword(passwordEncoder.encode(guardian.getPassword()));
        return guardianRepository.save(guardian);
    }

    @CacheEvict(value = {"guardians", "guardianById", "animals", "animalById"}, allEntries = true)
    public GuardianResponseDTO update(Integer id, GuardianRequestDTO dto) {
        Guardian guardian = findEntityById(id);
        guardian.setName(dto.getName());
        guardian.setEmail(dto.getEmail());
        guardian.setPassword(passwordEncoder.encode(dto.getPassword()));
        return toResponse(guardianRepository.save(guardian));
    }

    @CacheEvict(value = {"guardians", "guardianById", "animals", "animalById"}, allEntries = true)
    public void deleteById(Integer id) {
        if (!guardianRepository.existsById(id)) throw notFound();
        guardianRepository.deleteById(id);
    }

    private GuardianResponseDTO toResponse(Guardian guardian) {
        return new GuardianResponseDTO(guardian.getId(), guardian.getName(), guardian.getEmail());
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Guardian not found");
    }
}