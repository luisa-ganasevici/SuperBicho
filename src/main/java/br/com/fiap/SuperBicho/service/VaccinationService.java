package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AnimalVaccinationRequestDTO;
import br.com.fiap.SuperBicho.dto.response.*;
import br.com.fiap.SuperBicho.entity.*;
import br.com.fiap.SuperBicho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class VaccinationService {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final VaccineRepository vaccineRepository;
    private final AnimalVaccinationRepository animalVaccinationRepository;
    private final AnimalRepository animalRepository;

    public List<VaccineResponseDTO> findAllVaccines() {
        return vaccineRepository.findAllByOrderByNameAsc().stream().map(this::toVaccineResponse).toList();
    }

    public List<AnimalVaccinationResponseDTO> findByAnimal(Integer animalId) {
        return animalVaccinationRepository.findByAnimalIdOrderByApplicationDateDesc(animalId).stream()
                .map(this::toResponse)
                .toList();
    }

    public AnimalVaccinationResponseDTO registerApplication(AnimalVaccinationRequestDTO dto) {
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> notFound("Animal"));
        Vaccine vaccine = vaccineRepository.findById(dto.getVaccineId())
                .orElseThrow(() -> notFound("Vacina"));

        AnimalVaccination vaccination = new AnimalVaccination();
        vaccination.setAnimal(animal);
        vaccination.setVaccine(vaccine);
        vaccination.setApplicationDate(dto.getApplicationDate());
        return toResponse(animalVaccinationRepository.save(vaccination));
    }

    public List<VaccineCalendarItemDTO> buildCalendar(Integer animalId) {
        List<Vaccine> vaccines = vaccineRepository.findAllByOrderByNameAsc();
        List<AnimalVaccination> applications = animalVaccinationRepository.findByAnimalId(animalId);
        String todayStr = LocalDate.now().format(ISO);

        return vaccines.stream().map(vaccine -> {
            Optional<AnimalVaccination> lastApplication = applications.stream()
                    .filter(a -> a.getVaccine().getId().equals(vaccine.getId()))
                    .max(Comparator.comparing(a -> parseDate(a.getApplicationDate())));

            if (lastApplication.isEmpty()) {
                return new VaccineCalendarItemDTO(vaccine.getId(), vaccine.getName(), vaccine.getDescription(),
                        null, null, "NUNCA_APLICADA", false);
            }

            String lastDateStr = lastApplication.get().getApplicationDate();
            boolean appliedToday = todayStr.equals(lastDateStr);

            if (vaccine.getBoosterIntervalMonths() == null) {
                return new VaccineCalendarItemDTO(vaccine.getId(), vaccine.getName(), vaccine.getDescription(),
                        lastDateStr, null, "DOSE_UNICA_CONCLUIDA", appliedToday);
            }

            LocalDate nextDue = parseDate(lastDateStr).plusMonths(vaccine.getBoosterIntervalMonths());
            LocalDate today = LocalDate.now();
            String status;
            if (nextDue.isBefore(today)) {
                status = "ATRASADA";
            } else if (!nextDue.isAfter(today.plusDays(30))) {
                status = "PROXIMA";
            } else {
                status = "EM_DIA";
            }

            return new VaccineCalendarItemDTO(vaccine.getId(), vaccine.getName(), vaccine.getDescription(),
                    lastDateStr, nextDue.format(ISO), status, appliedToday);
        }).toList();
    }

    public void desfazerAplicacaoHoje(Integer animalId, Integer vaccineId) {
        String todayStr = LocalDate.now().format(ISO);
        animalVaccinationRepository.findByAnimalId(animalId).stream()
                .filter(a -> a.getVaccine().getId().equals(vaccineId) && todayStr.equals(a.getApplicationDate()))
                .max(Comparator.comparing(AnimalVaccination::getId))
                .ifPresent(a -> animalVaccinationRepository.deleteById(a.getId()));
    }

    public List<VaccineAlertDTO> buildAlertsForGuardian(List<AnimalResponseDTO> animals) {
        return animals.stream()
                .flatMap(animal -> buildCalendar(animal.getId()).stream()
                        .filter(item -> "ATRASADA".equals(item.getCalendarStatus())
                                || "PROXIMA".equals(item.getCalendarStatus())
                                || "NUNCA_APLICADA".equals(item.getCalendarStatus()))
                        .map(item -> new VaccineAlertDTO(animal.getId(), animal.getName(), item.getVaccineName(),
                                item.getNextDueDate(), item.getCalendarStatus())))
                .toList();
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, ISO);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Data de vacinação inválida: " + value);
        }
    }

    private VaccineResponseDTO toVaccineResponse(Vaccine vaccine) {
        return new VaccineResponseDTO(vaccine.getId(), vaccine.getName(), vaccine.getSpecies(),
                vaccine.getDescription(), vaccine.getFirstDoseAgeMonths(), vaccine.getBoosterIntervalMonths());
    }

    private AnimalVaccinationResponseDTO toResponse(AnimalVaccination vaccination) {
        return new AnimalVaccinationResponseDTO(vaccination.getId(), vaccination.getAnimal().getId(),
                vaccination.getVaccine().getId(), vaccination.getVaccine().getName(), vaccination.getApplicationDate());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found");
    }
}