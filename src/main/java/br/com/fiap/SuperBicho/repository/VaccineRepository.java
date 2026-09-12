package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Vaccine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineRepository extends JpaRepository<Vaccine, Integer> {
    List<Vaccine> findAllByOrderByNameAsc();
}