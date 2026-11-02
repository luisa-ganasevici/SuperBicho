package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AppointmentResponseDTO;
import br.com.fiap.SuperBicho.entity.*;
import br.com.fiap.SuperBicho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service @RequiredArgsConstructor
public class AppointmentService {

    public static final String STATUS_SCHEDULED = "AGENDADO";
    public static final String STATUS_CANCELED = "CANCELADO";
    public static final String STATUS_COMPLETED = "CONCLUIDO";

    private final AppointmentRepository appointmentRepository;
    private final HistoryRepository historyRepository;
    private final AnimalService animalService;
    private final ClinicRepository clinicRepository;
    private final VeterinarianRepository veterinarianRepository;

    public AppointmentResponseDTO findByIdForGuardian(Integer id, Integer guardianId) {
        return toResponse(findOwnedEntity(id, guardianId)); }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public AppointmentResponseDTO create(Integer guardianId, AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setStatus(STATUS_SCHEDULED);
        Appointment saved = appointmentRepository.save(copyFields(appointment, guardianId, dto));

        History history = new History();
        history.setDescription("Appointment was scheduled");
        history.setType("APPOINTMENT");
        history.setRecordDate(saved.getDate());
        history.setAnimal(saved.getAnimal());
        historyRepository.save(history);

        return toResponse(saved);
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public AppointmentResponseDTO update(Integer id, Integer guardianId, AppointmentRequestDTO dto) {
        Appointment appointment = findOwnedEntity(id, guardianId);
        if (!STATUS_SCHEDULED.equals(appointment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only scheduled appointments can be changed");
        }
        return toResponse(appointmentRepository.save(copyFields(appointment, guardianId, dto)));
    }

    public Appointment findEntityById(Integer id) {
        return appointmentRepository.findById(id).orElseThrow(() -> notFound("Appointment"));
    }

    private Appointment findOwnedEntity(Integer id, Integer guardianId) {
        Appointment appointment = findEntityById(id);
        Animal animal = appointment.getAnimal();
        if (animal == null || !animal.getGuardian().getId().equals(guardianId)) throw forbidden();
        return appointment;
    }

    private Appointment copyFields(Appointment appointment, Integer guardianId, AppointmentRequestDTO dto) {
        appointment.setDate(dto.getDate());
        appointment.setTime(dto.getTime());
        Animal animal = animalService.findOwnedEntity(dto.getAnimalId(), guardianId);
        appointment.setAnimal(animal);
        appointment.setAnimalName(animal.getName());
        appointment.setGuardianName(animal.getGuardian().getName());
        appointment.setClinic(resolveClinic(dto.getClinicId()));
        appointment.setVeterinarian(resolveVeterinarian(dto.getVeterinarianId()));
        return appointment;
    }

    private Clinic resolveClinic(Integer clinicId) {
        return clinicRepository.findById(clinicId).orElseThrow(() -> notFound("Clinic"));
    }

    private Veterinarian resolveVeterinarian(Integer veterinarianId) {
        if (veterinarianId == null) return null;
        return veterinarianRepository.findById(veterinarianId).orElseThrow(() -> notFound("Veterinarian"));
    }

    private AppointmentResponseDTO toResponse(Appointment appointment) {
        Animal animal = appointment.getAnimal();
        return new AppointmentResponseDTO(appointment.getId(), appointment.getDate(), appointment.getTime(),
                appointment.getStatus(), appointment.getCancelReason(),
                animal != null ? animal.getId() : null,
                animal != null ? animal.getName() : appointment.getAnimalName(),
                appointment.getClinic().getId(), appointment.getClinic().getName(),
                appointment.getVeterinarian() != null ? appointment.getVeterinarian().getId() : null,
                appointment.getVeterinarian() != null ? appointment.getVeterinarian().getName() : null,
                animal != null ? animal.getGuardian().getName() : appointment.getGuardianName());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found");
    }

    private ResponseStatusException forbidden() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to change this appointment");
    }

    public List<AppointmentResponseDTO> findByGuardian(Integer guardianId) {
        return appointmentRepository.findByAnimal_GuardianId(guardianId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponseDTO> findByClinic(Integer clinicId) {
        return appointmentRepository.findByClinicId(clinicId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponseDTO> findByAnimal(Integer animalId) {
        return appointmentRepository.findByAnimalId(animalId).stream()
                .map(this::toResponse)
                .toList();
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public AppointmentResponseDTO cancelByGuardian(Integer id, Integer guardianId, String reason) {
        Appointment appointment = findOwnedEntity(id, guardianId);
        appointment.setStatus(STATUS_CANCELED);
        appointment.setCancelReason(reason);
        return toResponse(appointmentRepository.save(appointment));
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public AppointmentResponseDTO cancelByClinic(Integer id, Integer clinicId, String reason) {
        Appointment appointment = findEntityById(id);
        if (!appointment.getClinic().getId().equals(clinicId)) throw forbidden();
        appointment.setStatus(STATUS_CANCELED);
        appointment.setCancelReason(reason);
        return toResponse(appointmentRepository.save(appointment));
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public AppointmentResponseDTO completeByClinic(Integer id, Integer clinicId) {
        Appointment appointment = findEntityById(id);
        if (!appointment.getClinic().getId().equals(clinicId)) throw forbidden();
        appointment.setStatus(STATUS_COMPLETED);
        return toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public void cancelOpenByAnimal(Integer animalId, String reason) {
        appointmentRepository.updateStatusByAnimal(animalId, STATUS_SCHEDULED, STATUS_CANCELED, reason);
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public void cancelAllByClinic(Integer clinicId, String reason) {
        appointmentRepository.findByClinicId(clinicId).stream()
                .filter(a -> STATUS_SCHEDULED.equals(a.getStatus()))
                .forEach(a -> {
                    a.setStatus(STATUS_CANCELED);
                    a.setCancelReason(reason);
                    appointmentRepository.save(a);
                });
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public void cancelAllByVeterinarian(Integer veterinarianId, String reason) {
        appointmentRepository.findByVeterinarianId(veterinarianId).stream()
                .filter(a -> STATUS_SCHEDULED.equals(a.getStatus()))
                .forEach(a -> {
                    a.setStatus(STATUS_CANCELED);
                    a.setCancelReason(reason);
                    a.setVeterinarian(null);
                    appointmentRepository.save(a);
                });
    }
}