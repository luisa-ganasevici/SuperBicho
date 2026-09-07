package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Animal;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    @Query(value = "SELECT a FROM Animal a JOIN FETCH a.guardian", countQuery = "SELECT COUNT(a) FROM Animal a")
    Page<Animal> findAllWithGuardian(Pageable pageable);
    List<Animal> findByGuardianId(Integer guardianId);
    List<Animal> findBySpecies(String species);
}
