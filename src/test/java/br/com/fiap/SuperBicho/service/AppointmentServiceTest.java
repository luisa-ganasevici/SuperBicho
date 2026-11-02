package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.AppointmentRequestDTO;
import br.com.fiap.SuperBicho.dto.response.AppointmentResponseDTO;
import br.com.fiap.SuperBicho.entity.*;
import br.com.fiap.SuperBicho.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private AnimalService animalService;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Guardian guardian;
    private Animal animal;
    private Clinic clinic;
    private Appointment appointment;
    private AppointmentRequestDTO appointmentRequestDTO;

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

        appointmentRequestDTO = new AppointmentRequestDTO("2026-10-10", "14:00", 1, 1, null);

        appointment = new Appointment(1, "2026-10-10", "14:00", "AGENDADO", null, animal, clinic, null, "Rex", "Maria Silva");
    }

    @Test
    public void deveAgendarConsultaComSucesso() {
        // arrange
        when(animalService.findOwnedEntity(1, 1)).thenReturn(animal);
        when(clinicRepository.findById(1)).thenReturn(Optional.of(clinic));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // act
        AppointmentResponseDTO response = appointmentService.create(1, appointmentRequestDTO);

        // assert
        assertEquals("AGENDADO", response.getStatus());
        assertEquals("Rex", response.getAnimalName());
        assertEquals("Maria Silva", response.getGuardianName());
        verify(historyRepository, times(1)).save(any(History.class));
    }

    @Test
    public void deveLancarExcecaoAoAgendarComAnimalInexistente() {
        // arrange
        when(animalService.findOwnedEntity(1, 1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found"));

        // act + assert
        assertThrows(ResponseStatusException.class, () -> appointmentService.create(1, appointmentRequestDTO));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void deveImpedirAgendarConsultaParaPetDeOutroTutor() {
        // arrange
        when(animalService.findOwnedEntity(1, 99)).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "This pet does not belong to you"));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.create(99, appointmentRequestDTO));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void deveImpedirVisualizarConsultaDeOutroTutor() {
        // arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.findByIdForGuardian(1, 99));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    public void deveImpedirAlterarConsultaDeOutroTutor() {
        // arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.update(1, 99, appointmentRequestDTO));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void deveImpedirAlterarConsultaJaCancelada() {
        // arrange
        appointment.setStatus("CANCELADO");
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

        // act + assert
        assertThrows(ResponseStatusException.class, () -> appointmentService.update(1, 1, appointmentRequestDTO));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void deveCancelarConsultaComoTutorDonoDoPet() {
        // arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        AppointmentResponseDTO response = appointmentService.cancelByGuardian(1, 1, "Imprevisto");

        // assert
        assertEquals("CANCELADO", response.getStatus());
        assertEquals("Imprevisto", response.getCancelReason());
    }

    @Test
    public void deveLancarExcecaoAoCancelarConsultaDeOutroTutor() {
        // arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.cancelByGuardian(1, 99, "Imprevisto"));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void deveConcluirConsultaComoClinicaResponsavel() {
        // arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        AppointmentResponseDTO response = appointmentService.completeByClinic(1, 1);

        // assert
        assertEquals("CONCLUIDO", response.getStatus());
    }

    @Test
    public void deveLancarExcecaoAoConcluirConsultaDeOutraClinica() {
        // arrange
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

        // act + assert
        assertThrows(ResponseStatusException.class, () -> appointmentService.completeByClinic(1, 99));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    public void deveCancelarConsultasEmAbertoDoPet() {
        // act
        appointmentService.cancelOpenByAnimal(1, "Pet removido pelo tutor");

        // assert
        verify(appointmentRepository, times(1))
                .updateStatusByAnimal(1, "AGENDADO", "CANCELADO", "Pet removido pelo tutor");
    }

    @Test
    public void deveManterNomesNaConsultaDePetExcluido() {
        // arrange
        Appointment semPet = new Appointment(3, "2026-10-10", "14:00", "CANCELADO", "Pet removido pelo tutor",
                null, clinic, null, "Rex", "Maria Silva");
        when(appointmentRepository.findByClinicId(1)).thenReturn(List.of(semPet));

        // act
        AppointmentResponseDTO response = appointmentService.findByClinic(1).get(0);

        // assert
        assertNull(response.getAnimalId());
        assertEquals("Rex", response.getAnimalName());
        assertEquals("Maria Silva", response.getGuardianName());
    }

    @Test
    public void deveImpedirTutorDeAcessarConsultaDePetExcluido() {
        // arrange
        appointment.setAnimal(null);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));

        // act + assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> appointmentService.findByIdForGuardian(1, 1));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    public void deveCancelarApenasConsultasAgendadasAoCancelarTodasDaClinica() {
        // arrange
        Appointment agendada = new Appointment(1, "2026-10-10", "14:00", "AGENDADO", null, animal, clinic, null, "Rex", "Maria Silva");
        Appointment concluida = new Appointment(2, "2026-09-01", "09:00", "CONCLUIDO", null, animal, clinic, null, "Rex", "Maria Silva");
        when(appointmentRepository.findByClinicId(1)).thenReturn(List.of(agendada, concluida));

        // act
        appointmentService.cancelAllByClinic(1, "Clinica fechada temporariamente");

        // assert
        assertEquals("CANCELADO", agendada.getStatus());
        assertEquals("CONCLUIDO", concluida.getStatus());
        verify(appointmentRepository, times(1)).save(agendada);
        verify(appointmentRepository, never()).save(concluida);
    }
}