package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.Adm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface AdmRepository extends JpaRepository<Adm, Integer> {
    Optional<Adm> findByEmail(String email);
}