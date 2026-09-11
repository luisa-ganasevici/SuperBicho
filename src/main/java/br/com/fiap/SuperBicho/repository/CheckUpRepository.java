package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.CheckUp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckUpRepository extends JpaRepository<CheckUp, Integer> {
    List<CheckUp> findByAnimal_GuardianId(Integer guardianId);
    List<CheckUp> findByClinicId(Integer clinicId);
    List<CheckUp> findByAnimalId(Integer animalId);
}