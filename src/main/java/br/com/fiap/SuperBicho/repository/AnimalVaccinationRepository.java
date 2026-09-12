package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.AnimalVaccination;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalVaccinationRepository extends JpaRepository<AnimalVaccination, Integer> {
    List<AnimalVaccination> findByAnimalId(Integer animalId);
    List<AnimalVaccination> findByAnimalIdOrderByApplicationDateDesc(Integer animalId);
}