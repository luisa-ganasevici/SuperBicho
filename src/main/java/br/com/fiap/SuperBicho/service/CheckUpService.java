package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.CheckUpRequestDTO;
import br.com.fiap.SuperBicho.dto.response.CheckUpResponseDTO;
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

    @Cacheable("checkUps")
    public Page<CheckUpResponseDTO> findAll(Pageable pageable) {
        return checkUpRepository.findAll(pageable).map(this::toResponse); }

    @Cacheable(value = "checkUpById", key = "#id")
    public CheckUpResponseDTO findById(Integer id) {
        return toResponse(findEntityById(id)); }

    @CacheEvict(value = {"checkUps", "checkUpById", "history", "historyById", "animals", "animalById"}, allEntries = true)
    public CheckUpResponseDTO create(CheckUpRequestDTO dto) {
        CheckUp checkUp = new CheckUp();
        checkUp.setCheckUpType(dto.getCheckUpType());
        checkUp.setCheckUpDate(dto.getCheckUpDate());
        checkUp.setStatus(dto.getStatus());
        checkUp.setNotes(dto.getNotes());
        checkUp.setAnimal(resolveAnimal(dto.getAnimalId()));
        CheckUp saved = checkUpRepository.save(checkUp);

        History history = new History();
        history.setDescription("Check-up " + saved.getCheckUpType() + " was created");
        history.setType("CHECK_UP");
        history.setRecordDate(saved.getCheckUpDate());
        history.setAnimal(saved.getAnimal());
        historyRepository.save(history);

        return toResponse(saved);
    }

    @CacheEvict(value = {"checkUps", "checkUpById", "history", "historyById", "animals", "animalById"}, allEntries = true)
    public CheckUpResponseDTO update(Integer id, CheckUpRequestDTO dto) {
        CheckUp checkUp = findEntityById(id);
        checkUp.setCheckUpType(dto.getCheckUpType());
        checkUp.setCheckUpDate(dto.getCheckUpDate());
        checkUp.setStatus(dto.getStatus());
        checkUp.setNotes(dto.getNotes());
        checkUp.setAnimal(resolveAnimal(dto.getAnimalId()));
        return toResponse(checkUpRepository.save(checkUp));
    }

    @CacheEvict(value = {"checkUps", "checkUpById", "history", "historyById", "animals", "animalById"}, allEntries = true)
    public void deleteById(Integer id) {
        if (!checkUpRepository.existsById(id)) throw notFound("Check-up");
        checkUpRepository.deleteById(id);
    }

    public CheckUp findEntityById(Integer id) {
        return checkUpRepository.findById(id).orElseThrow(() -> notFound("Check-up"));
    }

    private Animal resolveAnimal(Integer animalId) {
        return animalRepository.findById(animalId).orElseThrow(() -> notFound("Animal"));
    }

    private CheckUpResponseDTO toResponse(CheckUp checkUp) {
        return new CheckUpResponseDTO(checkUp.getId(), checkUp.getCheckUpType(), checkUp.getCheckUpDate(), checkUp.getStatus(), checkUp.getNotes(), checkUp.getAnimal().getId());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found");
    }
}