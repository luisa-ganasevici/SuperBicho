package br.com.fiap.SuperBicho.repository;

import br.com.fiap.SuperBicho.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Integer> { }
