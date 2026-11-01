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
import org.springframework.http.HttpStatus;
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

        animalRequestDTO = new AnimalRequestDTO("Rex", "Cachorro", 3, 12.5);
    }

    @Test
    public void deveCadastrarAnimalComSucesso() {
        // arrange
        when(guardianRepository.findById(1)).thenReturn(Optional.of(guardian));
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);

        // act
        AnimalResponseDTO response = animalService.create(1, animalRequestDTO);

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
                () -> animalService.create(1, animalRequestDTO));
        assertTrue(exception.getReason().contains("Guardian"));
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    public void deveBuscarAnimalDoProprioTutor() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        // act
        AnimalResponseDTO response = animalService.findByIdForGuardian(1, 1);

        // assert
        assertEquals(1, response.getId());
        assertEquals("Rex", response.getName());
    }

    @Test
    public void deveImpedirBuscarAnimalDeOutroTutor() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> animalService.findByIdForGuardian(1, 99));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    public void deveLancarExcecaoAoBuscarAnimalInexistente() {
        // arrange
        when(animalRepository.findById(99)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class, () -> animalService.findByIdForGuardian(99, 1));
    }

    @Test
    public void deveImpedirEditarAnimalDeOutroTutor() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> animalService.update(1, 99, animalRequestDTO));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    public void deveExcluirAnimalDoProprioTutor() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        // act
        animalService.deleteById(1, 1);

        // assert
        verify(animalRepository, times(1)).delete(animal);
    }

    @Test
    public void deveImpedirExcluirAnimalDeOutroTutor() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> animalService.deleteById(1, 99));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(animalRepository, never()).delete(any(Animal.class));
    }
}
