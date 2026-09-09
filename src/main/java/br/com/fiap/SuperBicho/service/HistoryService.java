package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.HistoryRequestDTO;
import br.com.fiap.SuperBicho.dto.response.HistoryResponseDTO;
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
    public Page<HistoryResponseDTO> findAll(Pageable pageable) {
        return historyRepository.findAll(pageable).map(this::toResponse); }

    @Cacheable(value = "historyById", key = "#id")
    public HistoryResponseDTO findById(Integer id) {
        return toResponse(findEntityById(id)); }

    @CacheEvict(value = {"history", "historyById", "animals", "animalById"}, allEntries = true)
    public HistoryResponseDTO create(HistoryRequestDTO dto) {
        History history = new History();
        history.setDescription(dto.getDescription());
        history.setType(dto.getType());
        history.setRecordDate(dto.getRecordDate());
        history.setAnimal(resolveAnimal(dto.getAnimalId()));
        return toResponse(historyRepository.save(history));
    }

    @CacheEvict(value = {"history", "historyById", "animals", "animalById"}, allEntries = true)
    public HistoryResponseDTO update(Integer id, HistoryRequestDTO dto) {
        History history = findEntityById(id);
        history.setDescription(dto.getDescription());
        history.setType(dto.getType());
        history.setRecordDate(dto.getRecordDate());
        history.setAnimal(resolveAnimal(dto.getAnimalId()));
        return toResponse(historyRepository.save(history));
    }

    @CacheEvict(value = {"history", "historyById", "animals", "animalById"}, allEntries = true)
    public void deleteById(Integer id) {
        if (!historyRepository.existsById(id)) throw notFound("History");
        historyRepository.deleteById(id);
    }

    public History findEntityById(Integer id) {
        return historyRepository.findById(id).orElseThrow(() -> notFound("History"));
    }

    private Animal resolveAnimal(Integer animalId) {
        return animalRepository.findById(animalId).orElseThrow(() -> notFound("Animal"));
    }

    private HistoryResponseDTO toResponse(History history) {
        return new HistoryResponseDTO(history.getId(), history.getDescription(), history.getType(), history.getRecordDate(), history.getAnimal().getId());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found");
    }
}