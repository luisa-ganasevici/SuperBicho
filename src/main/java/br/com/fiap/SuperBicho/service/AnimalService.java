package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.AnimalDTO;
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
    public Page<Animal> findAll(Pageable pageable) {
        return animalRepository.findAllWithGuardian(pageable); }

    @Cacheable(value = "animalsBySpecies", key = "#species")
    public List<Animal> findBySpecies(String species) {
        return animalRepository.findBySpecies(species); }

    @Cacheable(value = "animalsByGuardian", key = "#guardianId")
    public List<Animal> findByGuardian(Integer guardianId) {
        return animalRepository.findByGuardianId(guardianId); }

    @Cacheable(value = "animalsBySpecies", key = "#species + '-' + #pageable.pageNumber")
    public Page<Animal> findBySpecies(String species, Pageable pageable) {
        return animalRepository.findBySpecies(species, pageable); }


    @Cacheable(value = "animalById", key = "#id")
    public Animal findById(Integer id) {
        return animalRepository.findById(id).orElseThrow(() -> notFound("Animal")); }

    @CacheEvict(value = {"animals", "animalsBySpecies", "animalsByGuardian", "animalById"}, allEntries = true)
    public Animal create(AnimalDTO dto) { return animalRepository.save(toEntity(new Animal(), dto)); }

    @CacheEvict(value = {"animals", "animalsBySpecies", "animalsByGuardian", "animalById"}, allEntries = true)
    public Animal update(Integer id, AnimalDTO dto) { return animalRepository.save(toEntity(findById(id), dto)); }

    @CacheEvict(value = {"animals", "animalsBySpecies", "animalsByGuardian", "animalById"}, allEntries = true)
    public void deleteById(Integer id) { if (!animalRepository.existsById(id)) throw notFound("Animal"); animalRepository.deleteById(id); }

    private Animal toEntity(Animal animal, AnimalDTO dto) {
        Guardian guardian = guardianRepository.findById(dto.getGuardianId()).orElseThrow(() -> notFound("Guardian"));
        animal.setName(dto.getName()); animal.setSpecies(dto.getSpecies()); animal.setAge(dto.getAge()); animal.setWeight(dto.getWeight()); animal.setGuardian(guardian);
        return animal;
    }
    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found"); }

    public Animal createForGuardian(AnimalDTO dto, Integer guardianId) {
        dto.setGuardianId(guardianId);
        return create(dto);
    }
}
