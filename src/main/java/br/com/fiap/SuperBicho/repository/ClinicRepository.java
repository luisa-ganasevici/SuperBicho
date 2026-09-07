package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Integer> {
    Optional<Clinic> findByEmail(String email);


}
