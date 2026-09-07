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
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final AnimalRepository animalRepository;

    @Cacheable("history")
    public Page<History> findAll(Pageable pageable) {
        return historyRepository.findAll(pageable); }

    @Cacheable(value = "historyById", key = "#id")
    public History findById(Integer id) {
        return historyRepository.findById(id).orElseThrow(() -> notFound("History")); }

    @CacheEvict(value = {"history", "historyById", "animals", "animalById"}, allEntries = true)
    public History create(History history) { history.setAnimal(resolveAnimal(history.getAnimal()));
        return historyRepository.save(history); }

    @CacheEvict(value = {"history", "historyById", "animals", "animalById"}, allEntries = true)

    public History update(Integer id, History updatedHistory) { History history = findById(id);
        history.setDescription(updatedHistory.getDescription());
        history.setType(updatedHistory.getType());
        history.setRecordDate(updatedHistory.getRecordDate());
        if (updatedHistory.getAnimal() != null && updatedHistory.getAnimal().getId() != null)
            history.setAnimal(resolveAnimal(updatedHistory.getAnimal()));
        return historyRepository.save(history); }

    @CacheEvict(value = {"history", "historyById", "animals", "animalById"}, allEntries = true)
    public void deleteById(Integer id) { if (!historyRepository.existsById(id)) throw notFound("History");
        historyRepository.deleteById(id); }

    private Animal resolveAnimal(Animal animal) { if (animal == null || animal.getId() == null)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Animal is required");
        return animalRepository.findById(animal.getId()).orElseThrow(() -> notFound("Animal")); }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found"); }
}
