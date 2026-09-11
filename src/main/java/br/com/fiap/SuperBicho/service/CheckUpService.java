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

import java.util.List;

@Service @RequiredArgsConstructor
public class CheckUpService {

    public static final String STATUS_PENDING = "PENDENTE";
    public static final String STATUS_CANCELED = "CANCELADO";
    public static final String STATUS_COMPLETED = "CONCLUIDO";

    private final CheckUpRepository checkUpRepository;
    private final HistoryRepository historyRepository;
    private final AnimalRepository animalRepository;
    private final ClinicRepository clinicRepository;
    private final VeterinarianRepository veterinarianRepository;

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
        checkUp.setAnimal(resolveAnimal(dto.getAnimalId()));
        checkUp.setClinic(resolveClinic(dto.getClinicId()));
        checkUp.setVeterinarian(resolveVeterinarian(dto.getVeterinarianId()));
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
        checkUp.setAnimal(resolveAnimal(dto.getAnimalId()));
        checkUp.setClinic(resolveClinic(dto.getClinicId()));
        checkUp.setVeterinarian(resolveVeterinarian(dto.getVeterinarianId()));
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

    private Clinic resolveClinic(Integer clinicId) {
        if (clinicId == null) return null;
        return clinicRepository.findById(clinicId).orElseThrow(() -> notFound("Clinic"));
    }

    private Veterinarian resolveVeterinarian(Integer veterinarianId) {
        if (veterinarianId == null) return null;
        return veterinarianRepository.findById(veterinarianId).orElseThrow(() -> notFound("Veterinarian"));
    }

    private CheckUpResponseDTO toResponse(CheckUp checkUp) {
        return new CheckUpResponseDTO(checkUp.getId(), checkUp.getCheckUpType(), checkUp.getCheckUpDate(),
                checkUp.getStatus(), checkUp.getCancelReason(),
                checkUp.getAnimal().getId(), checkUp.getAnimal().getName(),
                checkUp.getClinic() != null ? checkUp.getClinic().getId() : null,
                checkUp.getClinic() != null ? checkUp.getClinic().getName() : null,
                checkUp.getVeterinarian() != null ? checkUp.getVeterinarian().getId() : null,
                checkUp.getVeterinarian() != null ? checkUp.getVeterinarian().getName() : null);
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found");
    }

    private ResponseStatusException forbidden() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to change this check-up");
    }

    public List<CheckUpResponseDTO> findByGuardian(Integer guardianId) {
        return checkUpRepository.findByAnimal_GuardianId(guardianId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CheckUpResponseDTO> findByClinic(Integer clinicId) {
        return checkUpRepository.findByClinicId(clinicId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CheckUpResponseDTO> findByAnimal(Integer animalId) {
        return checkUpRepository.findByAnimalId(animalId).stream()
                .map(this::toResponse)
                .toList();
    }

    @CacheEvict(value = {"checkUps", "checkUpById"}, allEntries = true)
    public CheckUpResponseDTO cancelByGuardian(Integer id, Integer guardianId, String reason) {
        CheckUp checkUp = findEntityById(id);
        if (!checkUp.getAnimal().getGuardian().getId().equals(guardianId)) throw forbidden();
        checkUp.setStatus(STATUS_CANCELED);
        checkUp.setCancelReason(reason);
        return toResponse(checkUpRepository.save(checkUp));
    }

    @CacheEvict(value = {"checkUps", "checkUpById"}, allEntries = true)
    public CheckUpResponseDTO completeByClinic(Integer id, Integer clinicId) {
        CheckUp checkUp = findEntityById(id);
        if (checkUp.getClinic() == null || !checkUp.getClinic().getId().equals(clinicId)) throw forbidden();
        checkUp.setStatus(STATUS_COMPLETED);
        return toResponse(checkUpRepository.save(checkUp));
    }

    @CacheEvict(value = {"checkUps", "checkUpById"}, allEntries = true)
    public void cancelAllByClinic(Integer clinicId, String reason) {
        checkUpRepository.findByClinicId(clinicId).stream()
                .filter(c -> STATUS_PENDING.equals(c.getStatus()))
                .forEach(c -> {
                    c.setStatus(STATUS_CANCELED);
                    c.setCancelReason(reason);
                    checkUpRepository.save(c);
                });
    }

    @CacheEvict(value = {"checkUps", "checkUpById"}, allEntries = true)
    public void cancelAllByVeterinarian(Integer veterinarianId, String reason) {
        checkUpRepository.findByVeterinarianId(veterinarianId).stream()
                .filter(c -> STATUS_PENDING.equals(c.getStatus()))
                .forEach(c -> {
                    c.setStatus(STATUS_CANCELED);
                    c.setCancelReason(reason);
                    c.setVeterinarian(null);
                    checkUpRepository.save(c);
                });
    }
}