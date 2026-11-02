package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByAnimal_GuardianId(Integer guardianId);
    List<Appointment> findByClinicId(Integer clinicId);
    List<Appointment> findByAnimalId(Integer animalId);
    List<Appointment> findByVeterinarianId(Integer veterinarianId);

    @Modifying
    @Query("UPDATE Appointment e SET e.status = :newStatus, e.cancelReason = :reason " +
           "WHERE e.animal.id = :animalId AND e.status = :currentStatus")
    int updateStatusByAnimal(@Param("animalId") Integer animalId,
                             @Param("currentStatus") String currentStatus,
                             @Param("newStatus") String newStatus,
                             @Param("reason") String reason);
}
