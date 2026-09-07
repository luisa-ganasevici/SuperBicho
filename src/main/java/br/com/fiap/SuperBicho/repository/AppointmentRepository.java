package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> { }
