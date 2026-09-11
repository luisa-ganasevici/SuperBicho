package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByAnimal_GuardianId(Integer guardianId);
    List<Appointment> findByClinicId(Integer clinicId);
    List<Appointment> findByAnimalId(Integer animalId);
}
