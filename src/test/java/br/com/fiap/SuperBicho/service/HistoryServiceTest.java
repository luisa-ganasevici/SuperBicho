package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.HistoryRequestDTO;
import br.com.fiap.SuperBicho.dto.response.HistoryResponseDTO;
import br.com.fiap.SuperBicho.entity.Animal;
import br.com.fiap.SuperBicho.entity.History;
import br.com.fiap.SuperBicho.repository.AnimalRepository;
import br.com.fiap.SuperBicho.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private HistoryService historyService;

    private Animal animal;
    private History history;
    private HistoryRequestDTO historyRequestDTO;

    @BeforeEach
    public void arrange() {
        animal = new Animal();
        animal.setId(1);
        animal.setName("Rex");

        historyRequestDTO = new HistoryRequestDTO("Vacina aplicada", "VACCINE", "2026-10-10", 1);

        history = new History();
        history.setId(1);
        history.setDescription("Vacina aplicada");
        history.setType("VACCINE");
        history.setRecordDate("2026-10-10");
        history.setAnimal(animal);
    }

    @Test
    public void deveRegistrarHistoricoComSucesso() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));
        when(historyRepository.save(any(History.class))).thenReturn(history);

        // act
        HistoryResponseDTO response = historyService.create(historyRequestDTO);

        // assert
        assertEquals("Vacina aplicada", response.getDescription());
        assertEquals("VACCINE", response.getType());
        assertEquals(1, response.getAnimalId());
    }

    @Test
    public void deveLancarExcecaoAoRegistrarHistoricoComAnimalInexistente() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class, () -> historyService.create(historyRequestDTO));
        verify(historyRepository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoBuscarHistoricoInexistente() {
        // arrange
        when(historyRepository.findById(99)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class, () -> historyService.findById(99));
    }

    @Test
    public void deveExcluirHistoricoComSucesso() {
        // arrange
        when(historyRepository.existsById(1)).thenReturn(true);

        // act
        historyService.deleteById(1);

        // assert
        verify(historyRepository, times(1)).deleteById(1);
    }

    @Test
    public void deveLancarExcecaoAoExcluirHistoricoInexistente() {
        // arrange
        when(historyRepository.existsById(99)).thenReturn(false);

        // act + assert
        assertThrows(ResponseStatusException.class, () -> historyService.deleteById(99));
        verify(historyRepository, never()).deleteById(any());
    }
}