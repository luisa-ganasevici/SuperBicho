package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.VeterinarianRequestDTO;
import br.com.fiap.SuperBicho.dto.response.VeterinarianResponseDTO;
import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.entity.Veterinarian;
import br.com.fiap.SuperBicho.repository.ClinicRepository;
import br.com.fiap.SuperBicho.repository.VeterinarianRepository;
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
public class VeterinarianServiceTest {

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private ClinicService clinicService;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private VeterinarianService veterinarianService;

    private Clinic clinic;
    private Veterinarian veterinarian;
    private VeterinarianRequestDTO veterinarianRequestDTO;

    @BeforeEach
    public void arrange() {
        clinic = new Clinic();
        clinic.setId(1);
        clinic.setName("Clinica Boa Vida");

        veterinarian = new Veterinarian(1, "Dr. João", "Clínica Geral", clinic);

        veterinarianRequestDTO = new VeterinarianRequestDTO("Dr. João", "Clínica Geral", 1);
    }

    @Test
    public void deveCadastrarVeterinarioComSucesso() {
        // arrange
        when(clinicRepository.findById(1)).thenReturn(Optional.of(clinic));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(veterinarian);

        // act
        VeterinarianResponseDTO response = veterinarianService.create(veterinarianRequestDTO);

        // assert
        assertEquals("Dr. João", response.getName());
        assertEquals("Clínica Geral", response.getSpecialty());
        assertEquals(1, response.getClinicId());
    }

    @Test
    public void deveExcluirVeterinarioDaProprioClinica() {
        // arrange
        when(veterinarianRepository.findById(1)).thenReturn(Optional.of(veterinarian));

        // act
        veterinarianService.deleteByIdForClinic(1, 1);

        // assert
        verify(veterinarianRepository, times(1)).deleteById(1);
    }

    @Test
    public void deveLancarExcecaoAoExcluirVeterinarioDeOutraClinica() {
        // arrange
        when(veterinarianRepository.findById(1)).thenReturn(Optional.of(veterinarian));

        // act + assert
        assertThrows(ResponseStatusException.class,
                () -> veterinarianService.deleteByIdForClinic(1, 99));
        verify(veterinarianRepository, never()).deleteById(any());
    }

    @Test
    public void deveExcluirVeterinarioPeloAdminECancelarSuasConsultasENotificarClinica() {
        // arrange
        when(veterinarianRepository.findById(1)).thenReturn(Optional.of(veterinarian));

        // act
        veterinarianService.deleteByAdmin(1);

        // assert
        verify(appointmentService, times(1)).cancelAllByVeterinarian(eq(1), anyString());
        verify(clinicService, times(1)).setNotice(eq(1), anyString());
        verify(veterinarianRepository, times(1)).deleteById(1);
    }

    @Test
    public void deveLancarExcecaoAoExcluirVeterinarioInexistente() {
        // arrange
        when(veterinarianRepository.findById(99)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class, () -> veterinarianService.deleteByAdmin(99));
        verify(appointmentService, never()).cancelAllByVeterinarian(any(), any());
    }
}