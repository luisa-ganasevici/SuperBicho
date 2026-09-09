package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.entity.ClinicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Integer> {
    Optional<Clinic> findByEmail(String email);
    List<Clinic> findByClinicStatus(ClinicStatus status);

}
