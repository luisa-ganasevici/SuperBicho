package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.ClinicRequestDTO;
import br.com.fiap.SuperBicho.dto.response.ClinicResponseDTO;
import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.entity.ClinicStatus;
import br.com.fiap.SuperBicho.repository.ClinicRepository;
import java.util.List;
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
    public Page<ClinicResponseDTO> findAll(Pageable pageable) {
        return clinicRepository.findAll(pageable).map(this::toResponse); }

    @Cacheable(value = "clinicById", key = "#id")
    public ClinicResponseDTO findById(Integer id) {
        return toResponse(findEntityById(id)); }

    public Clinic findEntityById(Integer id) {
        return clinicRepository.findById(id).orElseThrow(() -> notFound()); }

    public Clinic findByEmail(String email) {
        return clinicRepository.findByEmail(email).orElseThrow(() -> notFound()); }

    public List<Clinic> findPending() {
        return clinicRepository.findByClinicStatus(ClinicStatus.PENDING); }

    @CacheEvict(value = {"clinics", "clinicById", "appointments", "appointmentById"}, allEntries = true)
    public ClinicResponseDTO create(ClinicRequestDTO dto) {
        Clinic clinic = new Clinic();
        clinic.setName(dto.getName());
        clinic.setAddress(dto.getAddress());
        clinic.setPhone(dto.getPhone());
        clinic.setEmail(dto.getEmail());
        clinic.setPassword(passwordEncoder.encode(dto.getPassword()));
        clinic.setCnpj(dto.getCnpj());
        clinic.setSpecialties(dto.getSpecialties());
        clinic.setClinicStatus(ClinicStatus.PENDING);
        return toResponse(clinicRepository.save(clinic));
    }

    @CacheEvict(value = {"clinics", "clinicById", "appointments", "appointmentById"}, allEntries = true)
    public Clinic create(Clinic clinic) {
        clinic.setPassword(passwordEncoder.encode(clinic.getPassword()));
        clinic.setClinicStatus(ClinicStatus.PENDING);
        return clinicRepository.save(clinic);
    }

    @CacheEvict(value = {"clinics", "clinicById", "appointments", "appointmentById"}, allEntries = true)
    public ClinicResponseDTO update(Integer id, ClinicRequestDTO dto) {
        Clinic clinic = findEntityById(id);
        clinic.setName(dto.getName());
        clinic.setAddress(dto.getAddress());
        clinic.setPhone(dto.getPhone());
        clinic.setEmail(dto.getEmail());
        clinic.setPassword(passwordEncoder.encode(dto.getPassword()));
        clinic.setCnpj(dto.getCnpj());
        clinic.setSpecialties(dto.getSpecialties());
        return toResponse(clinicRepository.save(clinic));
    }

    @CacheEvict(value = {"clinics", "clinicById", "appointments", "appointmentById"}, allEntries = true)
    public void deleteById(Integer id) {
        if (!clinicRepository.existsById(id)) throw notFound();
        clinicRepository.deleteById(id);
    }

    @CacheEvict(value = {"clinics", "clinicById"}, allEntries = true)
    public Clinic disable(Integer id) {
        Clinic clinic = findEntityById(id);
        clinic.setClinicStatus(ClinicStatus.REMOVED);
        clinic.setNotice("Sua clínica foi desativada do cadastro da SuperBicho. Caso tenha dúvidas, entre em contato conosco.");
        return clinicRepository.save(clinic);
    }

    @CacheEvict(value = {"clinics", "clinicById"}, allEntries = true)
    public void setNotice(Integer id, String notice) {
        Clinic clinic = findEntityById(id);
        clinic.setNotice(notice);
        clinicRepository.save(clinic);
    }

    @CacheEvict(value = {"clinics", "clinicById"}, allEntries = true)
    public void clearNotice(Integer id) {
        Clinic clinic = findEntityById(id);
        clinic.setNotice(null);
        clinicRepository.save(clinic);
    }

    @CacheEvict(value = {"clinics", "clinicById"}, allEntries = true)
    public Clinic approve(Integer id) {
        Clinic clinic = findEntityById(id);
        clinic.setClinicStatus(ClinicStatus.APPROVED);
        return clinicRepository.save(clinic);
    }

    @CacheEvict(value = {"clinics", "clinicById"}, allEntries = true)
    public Clinic deny(Integer id) {
        Clinic clinic = findEntityById(id);
        clinic.setClinicStatus(ClinicStatus.DENIED);
        return clinicRepository.save(clinic);
    }

    private ClinicResponseDTO toResponse(Clinic clinic) {
        return new ClinicResponseDTO(clinic.getId(), clinic.getName(), clinic.getAddress(), clinic.getPhone(), clinic.getEmail(), clinic.getClinicStatus(), clinic.getCnpj(), clinic.getSpecialties());
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Clinic not found");
    }

    public List<Clinic> findApproved() {
        return clinicRepository.findByClinicStatus(ClinicStatus.APPROVED);
    }
}