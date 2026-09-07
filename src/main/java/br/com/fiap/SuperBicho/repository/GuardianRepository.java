package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Guardian;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianRepository extends JpaRepository<Guardian, Integer> {
    Optional<Guardian> findByEmail(String email);
}
