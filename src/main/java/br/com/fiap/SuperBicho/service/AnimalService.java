package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AnimalResponseDTO;
import br.com.fiap.SuperBicho.entity.*;
import br.com.fiap.SuperBicho.repository.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final GuardianRepository guardianRepository;

    @Cacheable(value = "animalsByGuardian", key = "#guardianId")
    public List<AnimalResponseDTO> findByGuardian(Integer guardianId) {
        return animalRepository.findByGuardianId(guardianId).stream().map(this::toResponse).toList(); }

    public AnimalResponseDTO findByIdForGuardian(Integer id, Integer guardianId) {
        return toResponse(findOwnedEntity(id, guardianId)); }

    @CacheEvict(value = "animalsByGuardian", allEntries = true)
    public AnimalResponseDTO create(Integer guardianId, AnimalRequestDTO dto) {
        Guardian guardian = guardianRepository.findById(guardianId).orElseThrow(() -> notFound("Guardian"));
        Animal animal = new Animal();
        animal.setGuardian(guardian);
        return toResponse(animalRepository.save(copyFields(animal, dto))); }

    @CacheEvict(value = "animalsByGuardian", allEntries = true)
    public AnimalResponseDTO update(Integer id, Integer guardianId, AnimalRequestDTO dto) {
        return toResponse(animalRepository.save(copyFields(findOwnedEntity(id, guardianId), dto))); }

    public Animal findOwnedEntity(Integer id, Integer guardianId) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> notFound("Animal"));
        if (!animal.getGuardian().getId().equals(guardianId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you");
        }
        return animal;
    }

    private Animal copyFields(Animal animal, AnimalRequestDTO dto) {
        animal.setName(dto.getName()); animal.setSpecies(dto.getSpecies()); animal.setAge(dto.getAge()); animal.setWeight(dto.getWeight());
        return animal;
    }

    private AnimalResponseDTO toResponse(Animal animal) {
        return new AnimalResponseDTO(animal.getId(), animal.getName(), animal.getSpecies(), animal.getAge(), animal.getWeight(), animal.getGuardian().getId());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found"); }
}
