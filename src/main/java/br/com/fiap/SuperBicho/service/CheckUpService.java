package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.entity.*;
import br.com.fiap.SuperBicho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @RequiredArgsConstructor
public class CheckUpService {

    private final CheckUpRepository checkUpRepository;

    private final HistoryRepository historyRepository;

    private final AnimalRepository animalRepository;

    @Cacheable("checkUps") public Page<CheckUp> findAll(Pageable pageable) {
        return checkUpRepository.findAll(pageable); }

    @Cacheable(value = "checkUpById", key = "#id")
    public CheckUp findById(Integer id) {
        return checkUpRepository.findById(id).orElseThrow(() -> notFound("Check-up")); }

    @CacheEvict(value = {"checkUps", "checkUpById", "history", "historyById", "animals", "animalById"}, allEntries = true)

    public CheckUp create(CheckUp checkUp) { checkUp.setAnimal(resolveAnimal(checkUp.getAnimal()));

        CheckUp savedCheckUp = checkUpRepository.save(checkUp);

        History history = new History(); history.setDescription("Check-up " + checkUp.getCheckUpType() + " was created");

        history.setType("CHECK_UP"); history.setRecordDate(checkUp.getCheckUpDate());

        history.setAnimal(checkUp.getAnimal()); historyRepository.save(history); return savedCheckUp; }

    @CacheEvict(value = {"checkUps", "checkUpById", "history", "historyById", "animals", "animalById"}, allEntries = true)

    public CheckUp update(Integer id, CheckUp updatedCheckUp) { CheckUp checkUp = findById(id);
        checkUp.setCheckUpType(updatedCheckUp.getCheckUpType());
        checkUp.setCheckUpDate(updatedCheckUp.getCheckUpDate());
        checkUp.setStatus(updatedCheckUp.getStatus());
        checkUp.setNotes(updatedCheckUp.getNotes());
        if (updatedCheckUp.getAnimal() != null && updatedCheckUp.getAnimal().getId() != null) checkUp.setAnimal(resolveAnimal(updatedCheckUp.getAnimal()));
            return checkUpRepository.save(checkUp); }

    @CacheEvict(value = {"checkUps", "checkUpById", "history", "historyById", "animals", "animalById"}, allEntries = true)

    public void deleteById(Integer id) { if (!checkUpRepository.existsById(id)) throw notFound("Check-up"); checkUpRepository.deleteById(id); }

    private Animal resolveAnimal(Animal animal) { if (animal == null || animal.getId() == null)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Animal is required");
        return animalRepository.findById(animal.getId()).orElseThrow(() -> notFound("Animal")); }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found"); }
}
