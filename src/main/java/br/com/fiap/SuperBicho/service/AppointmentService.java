package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AppointmentResponseDTO;
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
public class AppointmentService {

    public static final String STATUS_SCHEDULED = "AGENDADO";
    public static final String STATUS_CANCELED = "CANCELADO";
    public static final String STATUS_COMPLETED = "CONCLUIDO";

    private final AppointmentRepository appointmentRepository;
    private final HistoryRepository historyRepository;
    private final AnimalRepository animalRepository;
    private final ClinicRepository clinicRepository;
    private final VeterinarianRepository veterinarianRepository;

    @Cacheable("appointments")
    public Page<AppointmentResponseDTO> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::toResponse); }

    @Cacheable(value = "appointmentById", key = "#id")
    public AppointmentResponseDTO findById(Integer id) {
        return toResponse(findEntityById(id)); }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setDate(dto.getDate());
        appointment.setTime(dto.getTime());
        appointment.setStatus(dto.getStatus());
        appointment.setAnimal(resolveAnimal(dto.getAnimalId()));
        appointment.setClinic(resolveClinic(dto.getClinicId()));
        appointment.setVeterinarian(resolveVeterinarian(dto.getVeterinarianId()));
        Appointment saved = appointmentRepository.save(appointment);

        History history = new History();
        history.setDescription("Appointment was scheduled");
        history.setType("APPOINTMENT");
        history.setRecordDate(saved.getDate());
        history.setAnimal(saved.getAnimal());
        historyRepository.save(history);

        return toResponse(saved);
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public AppointmentResponseDTO update(Integer id, AppointmentRequestDTO dto) {
        Appointment appointment = findEntityById(id);
        appointment.setDate(dto.getDate());
        appointment.setTime(dto.getTime());
        appointment.setStatus(dto.getStatus());
        appointment.setAnimal(resolveAnimal(dto.getAnimalId()));
        appointment.setClinic(resolveClinic(dto.getClinicId()));
        appointment.setVeterinarian(resolveVeterinarian(dto.getVeterinarianId()));
        return toResponse(appointmentRepository.save(appointment));
    }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public void deleteById(Integer id) {
        if (!appointmentRepository.existsById(id)) throw notFound("Appointment");
        appointmentRepository.deleteById(id);
    }

    public Appointment findEntityById(Integer id) {
        return appointmentRepository.findById(id).orElseThrow(() -> notFound("Appointment"));
    }

    private Animal resolveAnimal(Integer animalId) {
        return animalRepository.findById(animalId).orElseThrow(() -> notFound("Animal"));
    }

    private Clinic resolveClinic(Integer clinicId) {
        return clinicRepository.findById(clinicId).orElseThrow(() -> notFound("Clinic"));
    }

    private Veterinarian resolveVeterinarian(Integer veterinarianId) {
        if (veterinarianId == null) return null;
        return veterinarianRepository.findById(veterinarianId).orElseThrow(() -> notFound("Veterinarian"));
    }

    private AppointmentResponseDTO toResponse(Appointment appointment) {
        return new AppointmentResponseDTO(appointment.getId(), appointment.getDate(), appointment.getTime(),
                appointment.getStatus(), appointment.getCancelReason(),
                appointment.getAnimal().getId(), appointment.getAnimal().getName(),
                appointment.getClinic().getId(), appointment.getClinic().getName(),
                appointment.getVeterinarian() != null ? appointment.getVeterinarian().getId() : null,
                appointment.getVeterinarian() != null ? appointment.getVeterinarian().getName() : null,
                appointment.getAnimal().getGuardian().getName());
    }

    private CheckUpResponseDTO toResponse(CheckUp checkUp) {
        return new CheckUpResponseDTO(
                checkUp.getId(),
                checkUp.getCheckUpType(),
                checkUp.getCheckUpDate(),
                checkUp.getStatus(),
                checkUp.getCancelReason(),
                checkUp.getAnimal().getId(),
                checkUp.getAnimal().getName(),
                checkUp.getClinic() != null ? checkUp.getClinic().getId() : null,
                checkUp.getClinic() != null ? checkUp.getClinic().getName() : null,
                checkUp.getAnimal().getGuardian().getName()
        );
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
        Appointment appointment = findEntityById(id);
        if (!appointment.getAnimal().getGuardian().getId().equals(guardianId)) throw forbidden();
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