package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service @RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable("clinics")
    public Page<Clinic> findAll(Pageable pageable) {
        return clinicRepository.findAll(pageable); }

    @Cacheable(value = "clinicById", key = "#id")
    public Clinic findById(Integer id) {
        return clinicRepository.findById(id).orElseThrow(() -> notFound()); }

    @CacheEvict(value = {"clinics", "clinicById", "appointments", "appointmentById"}, allEntries = true)
    public Clinic create(Clinic clinic) {
        clinic.setPassword(passwordEncoder.encode(clinic.getPassword()));
        return clinicRepository.save(clinic); }

    @CacheEvict(value = {"clinics", "clinicById", "appointments", "appointmentById"}, allEntries = true)

    public Clinic update(Integer id, Clinic updatedClinic) { Clinic clinic = findById(id); clinic.setName(updatedClinic.getName());

        clinic.setAddress(updatedClinic.getAddress());

        clinic.setPhone(updatedClinic.getPhone());

        return clinicRepository.save(clinic); }

    @CacheEvict(value = {"clinics", "clinicById", "appointments", "appointmentById"}, allEntries = true)
    public void deleteById(Integer id) {
        if (!clinicRepository.existsById(id)) throw notFound();
        clinicRepository.deleteById(id); }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Clinic not found"); }
}
