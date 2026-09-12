package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.CheckUpRequestDTO;
import br.com.fiap.SuperBicho.dto.response.CheckUpResponseDTO;
import br.com.fiap.SuperBicho.entity.*;
import br.com.fiap.SuperBicho.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckUpServiceTest {

    @Mock
    private CheckUpRepository checkUpRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @InjectMocks
    private CheckUpService checkUpService;

    private Guardian guardian;
    private Animal animal;
    private Clinic clinic;
    private CheckUp checkUp;
    private CheckUpRequestDTO checkUpRequestDTO;

    @BeforeEach
    public void arrange() {
        guardian = new Guardian();
        guardian.setId(1);
        guardian.setName("Maria Silva");

        animal = new Animal();
        animal.setId(1);
        animal.setName("Rex");
        animal.setGuardian(guardian);

        clinic = new Clinic();
        clinic.setId(1);
        clinic.setName("Clinica Boa Vida");
        clinic.setClinicStatus(ClinicStatus.APPROVED);

        checkUpRequestDTO = new CheckUpRequestDTO();
        checkUpRequestDTO.setCheckUpType("Raio-X");
        checkUpRequestDTO.setCheckUpDate("2026-10-10");
        checkUpRequestDTO.setStatus("PENDENTE");
        checkUpRequestDTO.setAnimalId(1);
        checkUpRequestDTO.setClinicId(1);

        checkUp = new CheckUp(1, "Raio-X", "2026-10-10", "PENDENTE", null, animal, clinic);
    }

    @Test
    public void deveCadastrarExameComSucesso() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));
        when(clinicRepository.findById(1)).thenReturn(Optional.of(clinic));
        when(checkUpRepository.save(any(CheckUp.class))).thenReturn(checkUp);

        // act
        CheckUpResponseDTO response = checkUpService.create(checkUpRequestDTO);

        // assert
        assertEquals("Raio-X", response.getCheckUpType());
        assertEquals("PENDENTE", response.getStatus());
        assertEquals("Rex", response.getAnimalName());
        verify(historyRepository, times(1)).save(any(History.class));
    }

    @Test
    public void deveLancarExcecaoAoCadastrarExameComClinicaInexistente() {
        // arrange
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));
        when(clinicRepository.findById(1)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class, () -> checkUpService.create(checkUpRequestDTO));
        verify(checkUpRepository, never()).save(any());
    }

    @Test
    public void deveCancelarExameComoTutorDonoDoPet() {
        // arrange
        when(checkUpRepository.findById(1)).thenReturn(Optional.of(checkUp));
        when(checkUpRepository.save(any(CheckUp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        CheckUpResponseDTO response = checkUpService.cancelByGuardian(1, 1, "Pet melhorou");

        // assert
        assertEquals("CANCELADO", response.getStatus());
        assertEquals("Pet melhorou", response.getCancelReason());
    }

    @Test
    public void deveLancarExcecaoAoCancelarExameDeOutroTutor() {
        // arrange
        when(checkUpRepository.findById(1)).thenReturn(Optional.of(checkUp));

        // act + assert
        assertThrows(ResponseStatusException.class,
                () -> checkUpService.cancelByGuardian(1, 99, "Pet melhorou"));
        verify(checkUpRepository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoConcluirExameSemClinicaVinculada() {
        // arrange: exame criado sem clínica (campo opcional na entidade)
        CheckUp exameSemClinica = new CheckUp(2, "Raio-X", "2026-10-10", "PENDENTE", null, animal, null);
        when(checkUpRepository.findById(2)).thenReturn(Optional.of(exameSemClinica));

        // act + assert
        assertThrows(ResponseStatusException.class, () -> checkUpService.completeByClinic(2, 1));
    }

    @Test
    public void deveConcluirExameComoClinicaResponsavel() {
        // arrange
        when(checkUpRepository.findById(1)).thenReturn(Optional.of(checkUp));
        when(checkUpRepository.save(any(CheckUp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        CheckUpResponseDTO response = checkUpService.completeByClinic(1, 1);

        // assert
        assertEquals("CONCLUIDO", response.getStatus());
    }

    @Test
    public void deveCancelarApenasExamesPendentesAoCancelarTodosDaClinica() {
        // arrange
        CheckUp pendente = new CheckUp(1, "Raio-X", "2026-10-10", "PENDENTE", null, animal, clinic);
        CheckUp concluido = new CheckUp(2, "Ultrassom", "2026-08-01", "CONCLUIDO", null, animal, clinic);
        when(checkUpRepository.findByClinicId(1)).thenReturn(List.of(pendente, concluido));

        // act
        checkUpService.cancelAllByClinic(1, "Clinica fechada temporariamente");

        // assert
        assertEquals("CANCELADO", pendente.getStatus());
        assertEquals("CONCLUIDO", concluido.getStatus());
        verify(checkUpRepository, times(1)).save(pendente);
        verify(checkUpRepository, never()).save(concluido);
    }
}