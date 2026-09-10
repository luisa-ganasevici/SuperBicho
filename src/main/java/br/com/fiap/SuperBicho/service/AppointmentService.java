package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AppointmentResponseDTO;
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

    private final AppointmentRepository appointmentRepository;
    private final HistoryRepository historyRepository;
    private final AnimalRepository animalRepository;
    private final ClinicRepository clinicRepository;

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

    private AppointmentResponseDTO toResponse(Appointment appointment) {
        return new AppointmentResponseDTO(appointment.getId(), appointment.getDate(), appointment.getTime(),
                appointment.getStatus(), appointment.getAnimal().getId(), appointment.getAnimal().getName(),
                appointment.getClinic().getId(), appointment.getClinic().getName());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found");
    }

    public  List<AppointmentResponseDTO> findByGuardian(Integer guardianId) {
        return appointmentRepository.findByAnimal_GuardianId(guardianId).stream()
                .map(this::toResponse)
                .toList();
    }
}