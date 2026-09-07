package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GuardianService {

    private final GuardianRepository guardianRepository;

    @Cacheable("guardians")
    public Page<Guardian> findAll(Pageable pageable) {
        return guardianRepository.findAll(pageable); }

    @Cacheable(value = "guardianById", key = "#id")
    public Guardian findById(Integer id) {
        return guardianRepository.findById(id).orElseThrow(() -> notFound()); }

    @CacheEvict(value = {"guardians", "guardianById", "animals", "animalById"}, allEntries = true)
    public Guardian create(Guardian guardian) { return guardianRepository.save(guardian); }

    @CacheEvict(value = {"guardians", "guardianById", "animals", "animalById"}, allEntries = true)
    public Guardian update(Integer id, Guardian updatedGuardian) { Guardian guardian = findById(id);
        guardian.setName(updatedGuardian.getName());
        guardian.setEmail(updatedGuardian.getEmail());
        guardian.setPassword(updatedGuardian.getPassword());
        return guardianRepository.save(guardian); }

    @CacheEvict(value = {"guardians", "guardianById", "animals", "animalById"}, allEntries = true)
    public void deleteById(Integer id) { if (!guardianRepository.existsById(id)) throw notFound();
        guardianRepository.deleteById(id); }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Guardian not found"); }
}
