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
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final HistoryRepository historyRepository;

    private final AnimalRepository animalRepository;

    private final ClinicRepository clinicRepository;

    @Cacheable("appointments")
    public Page<Appointment> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable); }

    @Cacheable(value = "appointmentById", key = "#id")
    public Appointment findById(Integer id) {
        return appointmentRepository.findById(id).orElseThrow(() -> notFound("Appointment")); }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public Appointment create(Appointment appointment) {

        appointment.setAnimal(resolveAnimal(appointment.getAnimal()));

        appointment.setClinic(resolveClinic(appointment.getClinic()));

        Appointment savedAppointment = appointmentRepository.save(appointment);

        History history = new History();

        history.setDescription("Appointment was scheduled");

        history.setType("APPOINTMENT");

        history.setRecordDate(appointment.getDate());

        history.setAnimal(appointment.getAnimal());

        historyRepository.save(history);

        return savedAppointment; }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public Appointment update(Integer id, Appointment updatedAppointment) {

        Appointment appointment = findById(id);

        appointment.setDate(updatedAppointment.getDate());

        appointment.setTime(updatedAppointment.getTime());

        appointment.setStatus(updatedAppointment.getStatus());

        if (updatedAppointment.getAnimal() != null && updatedAppointment.getAnimal().getId() != null)
            appointment.setAnimal(resolveAnimal(updatedAppointment.getAnimal()));

        if (updatedAppointment.getClinic() != null && updatedAppointment.getClinic().getId() != null)
            appointment.setClinic(resolveClinic(updatedAppointment.getClinic()));

        return appointmentRepository.save(appointment); }

    @CacheEvict(value = {"appointments", "appointmentById"}, allEntries = true)
    public void deleteById(Integer id) { if (!appointmentRepository.existsById(id)) throw notFound
            ("Appointment"); appointmentRepository.deleteById(id); }

    private Animal resolveAnimal(Animal animal) { if (animal == null || animal.getId() == null)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Animal is required");
        return animalRepository.findById(animal.getId()).orElseThrow(() -> notFound("Animal")); }

    private Clinic resolveClinic(Clinic clinic) { if (clinic == null || clinic.getId() == null)
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clinic is required");
        return clinicRepository.findById(clinic.getId()).orElseThrow(() -> notFound("Clinic")); }

    private ResponseStatusException notFound(String resource)
    { return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found"); }
}
