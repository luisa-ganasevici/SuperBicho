package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Integer> { }
