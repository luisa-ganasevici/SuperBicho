package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AnimalResponseDTO;
import br.com.fiap.SuperBicho.entity.*;
import br.com.fiap.SuperBicho.repository.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final GuardianRepository guardianRepository;

    @Cacheable("animals")
    public Page<AnimalResponseDTO> findAll(Pageable pageable) {
        return animalRepository.findAllWithGuardian(pageable).map(this::toResponse); }

    @Cacheable(value = "animalsBySpecies", key = "#species + '-' + #pageable.pageNumber")
    public Page<AnimalResponseDTO> findBySpecies(String species, Pageable pageable) {
        return animalRepository.findBySpecies(species, pageable).map(this::toResponse); }

    @Cacheable(value = "animalsByGuardian", key = "#guardianId")
    public List<AnimalResponseDTO> findByGuardian(Integer guardianId) {
        return animalRepository.findByGuardianId(guardianId).stream().map(this::toResponse).toList(); }

    @Cacheable(value = "animalById", key = "#id")
    public AnimalResponseDTO findById(Integer id) {
        return toResponse(findEntityById(id)); }

    @CacheEvict(value = {"animals", "animalsBySpecies", "animalsByGuardian", "animalById"}, allEntries = true)
    public AnimalResponseDTO create(AnimalRequestDTO dto) {
        return toResponse(animalRepository.save(toEntity(new Animal(), dto))); }

    @CacheEvict(value = {"animals", "animalsBySpecies", "animalsByGuardian", "animalById"}, allEntries = true)
    public AnimalResponseDTO update(Integer id, AnimalRequestDTO dto) {
        return toResponse(animalRepository.save(toEntity(findEntityById(id), dto))); }

    @CacheEvict(value = {"animals", "animalsBySpecies", "animalsByGuardian", "animalById"}, allEntries = true)
    public void deleteById(Integer id) { if (!animalRepository.existsById(id)) throw notFound("Animal"); animalRepository.deleteById(id); }

    public Animal findEntityById(Integer id) {
        return animalRepository.findById(id).orElseThrow(() -> notFound("Animal")); }

    private Animal toEntity(Animal animal, AnimalRequestDTO dto) {
        Guardian guardian = guardianRepository.findById(dto.getGuardianId()).orElseThrow(() -> notFound("Guardian"));
        animal.setName(dto.getName()); animal.setSpecies(dto.getSpecies()); animal.setAge(dto.getAge()); animal.setWeight(dto.getWeight()); animal.setGuardian(guardian);
        return animal;
    }

    private AnimalResponseDTO toResponse(Animal animal) {
        return new AnimalResponseDTO(animal.getId(), animal.getName(), animal.getSpecies(), animal.getAge(), animal.getWeight(), animal.getGuardian().getId());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found"); }
}