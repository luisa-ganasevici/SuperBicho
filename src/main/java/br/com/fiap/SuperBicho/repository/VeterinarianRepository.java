package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Veterinarian;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Integer> {
    List<Veterinarian> findByClinicId(Integer clinicId);
}
