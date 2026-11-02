package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.entity.Animal;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalRemovalServiceTest {

    @Mock
    private AnimalService animalService;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private CheckUpService checkUpService;

    @InjectMocks
    private AnimalRemovalService animalRemovalService;

    private Animal animal;

    @BeforeEach
    public void arrange() {
        Guardian guardian = new Guardian();
        guardian.setId(1);

        animal = new Animal();
        animal.setId(1);
        animal.setName("Rex");
        animal.setGuardian(guardian);
    }

    @Test
    public void deveCancelarPendenciasEExcluirPetDoProprioTutor() {
        // arrange
        when(animalService.findOwnedEntity(1, 1)).thenReturn(animal);

        // act
        animalRemovalService.remove(1, 1);

        // assert
        InOrder ordem = inOrder(appointmentService, checkUpService, animalRepository);
        ordem.verify(appointmentService).cancelOpenByAnimal(1, AnimalRemovalService.REMOVAL_REASON);
        ordem.verify(checkUpService).cancelOpenByAnimal(1, AnimalRemovalService.REMOVAL_REASON);
        ordem.verify(animalRepository).delete(animal);
    }

    @Test
    public void deveImpedirExcluirPetDeOutroTutor() {
        // arrange
        when(animalService.findOwnedEntity(1, 99))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you"));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> animalRemovalService.remove(1, 99));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(appointmentService, never()).cancelOpenByAnimal(anyInt(), anyString());
        verify(checkUpService, never()).cancelOpenByAnimal(anyInt(), anyString());
        verify(animalRepository, never()).delete(any(Animal.class));
    }
}
