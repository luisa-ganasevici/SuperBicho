package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.CheckUp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckUpRepository extends JpaRepository<CheckUp, Integer> {
    List<CheckUp> findByAnimal_GuardianId(Integer guardianId);
    List<CheckUp> findByClinicId(Integer clinicId);
    List<CheckUp> findByAnimalId(Integer animalId);

    @Modifying
    @Query("UPDATE CheckUp e SET e.status = :newStatus, e.cancelReason = :reason " +
           "WHERE e.animal.id = :animalId AND e.status = :currentStatus")
    int updateStatusByAnimal(@Param("animalId") Integer animalId,
                             @Param("currentStatus") String currentStatus,
                             @Param("newStatus") String newStatus,
                             @Param("reason") String reason);
}
