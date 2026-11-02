package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.entity.Animal;
import br.com.fiap.SuperBicho.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AnimalRemovalService {

    public static final String REMOVAL_REASON = "Pet excluido";

    private final AnimalService animalService;
    private final AnimalRepository animalRepository;
    private final AppointmentService appointmentService;
    private final CheckUpService checkUpService;

    @Transactional
    @CacheEvict(value = {"animalsByGuardian", "appointments", "appointmentById", "checkUps", "checkUpById"}, allEntries = true)
    public void remove(Integer animalId, Integer guardianId) {
        Animal animal = animalService.findOwnedEntity(animalId, guardianId);
        appointmentService.cancelOpenByAnimal(animalId, REMOVAL_REASON);
        checkUpService.cancelOpenByAnimal(animalId, REMOVAL_REASON);
        animalRepository.delete(animal);
    }
}
