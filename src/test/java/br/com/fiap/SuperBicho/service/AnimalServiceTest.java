package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AnimalRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AnimalResponseDTO;
import br.com.fiap.SuperBicho.entity.Animal;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.repository.AnimalRepository;
import br.com.fiap.SuperBicho.repository.GuardianRepository;
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
public class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private GuardianRepository guardianRepository;

    @InjectMocks
    private AnimalService animalService;

    private Guardian guardian;
    private Animal animal;
    private AnimalRequestDTO animalRequestDTO;

    @BeforeEach
    public void arrange() {
        guardian = new Guardian();
        guardian.setId(1);
        guardian.setName("Maria Silva");
        guardian.setEmail("maria@email.com");
        guardian.setPassword("senha123");

        animal = new Animal();
        animal.setId(1);
        animal.setName("Rex");
        animal.setSpecies("Cachorro");
        animal.setAge(3);
        animal.setWeight(12.5);
        animal.setGuardian(guardian);

        animalRequestDTO = new AnimalRequestDTO("Rex", "Cachorro", 3, 12.5, 1);
    }

    @Test
    public void deveCadastrarAnimalComSucesso() {
        // arrange
        when(guardianRepository.findById(1)).thenReturn(Optional.of(guardian));
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);

        // act
        AnimalResponseDTO response = animalService.create(animalRequestDTO);

        // assert
        assertEquals("Rex", response.getName());
        assertEquals("Cachorro", response.getSpecies());
        assertEquals(3, response.getAge());
        assertEquals(12.5, response.getWeight());
        assertEquals(1, response.getGuardianId());
        verify(animalRepository, times(1)).save(any(Animal.class));
    }

    @Test
    public void deveLancarExcecaoAoCadastrarAnimalComGuardianInexistente() {
        // arrange
        when(guardianRepository.findById(1)).thenReturn(Optional.empty());

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> animalService.create(animalRequestDTO));
        assertTrue(exception.getReason().contains("Guardian"));
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    public void deveBuscarAnimalPorIdComSucesso() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        // act
        AnimalResponseDTO response = animalService.findById(1);

        // assert
        assertEquals(1, response.getId());
        assertEquals("Rex", response.getName());
    }

    @Test
    public void deveLancarExcecaoAoBuscarAnimalInexistente() {
        // arrange
        when(animalRepository.findById(99)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class, () -> animalService.findById(99));
    }

    @Test
    public void deveExcluirAnimalComSucesso() {
        // arrange
        when(animalRepository.existsById(1)).thenReturn(true);

        // act
        animalService.deleteById(1);

        // assert
        verify(animalRepository, times(1)).deleteById(1);
    }

    @Test
    public void deveLancarExcecaoAoExcluirAnimalInexistente() {
        // arrange
        when(animalRepository.existsById(99)).thenReturn(false);

        // act + assert
        assertThrows(ResponseStatusException.class, () -> animalService.deleteById(99));
        verify(animalRepository, never()).deleteById(any());
    }
}