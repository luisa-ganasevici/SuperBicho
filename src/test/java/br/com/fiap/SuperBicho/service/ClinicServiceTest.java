package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.ClinicRequestDTO;
import br.com.fiap.SuperBicho.dto.response.ClinicResponseDTO;
import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.entity.ClinicStatus;
import br.com.fiap.SuperBicho.repository.ClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClinicServiceTest {

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClinicService clinicService;

    private Clinic clinic;
    private ClinicRequestDTO clinicRequestDTO;

    @BeforeEach
    public void arrange() {
        clinic = new Clinic();
        clinic.setId(1);
        clinic.setName("Clinica Boa Vida");
        clinic.setAddress("Rua das Flores, 100");
        clinic.setPhone("11999990000");
        clinic.setEmail("clinica@email.com");
        clinic.setPassword("senhaCriptografada");
        clinic.setCnpj("12345678000199");
        clinic.setClinicStatus(ClinicStatus.PENDING);

        clinicRequestDTO = new ClinicRequestDTO("Clinica Boa Vida", "Rua das Flores, 100", "11999990000",
                "clinica@email.com", "senha123", "12345678000199", List.of("Clínica Geral"));
    }

    @Test
    public void deveCadastrarClinicaComoPendenteESenhaCriptografada() {
        // arrange
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        ClinicResponseDTO response = clinicService.create(clinicRequestDTO);

        // assert
        assertEquals("Clinica Boa Vida", response.getName());
        assertEquals(ClinicStatus.PENDING, response.getClinicStatus());
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    public void deveAprovarClinicaComSucesso() {
        // arrange
        when(clinicRepository.findById(1)).thenReturn(Optional.of(clinic));
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        Clinic aprovada = clinicService.approve(1);

        // assert
        assertEquals(ClinicStatus.APPROVED, aprovada.getClinicStatus());
    }

    @Test
    public void deveRecusarClinicaComSucesso() {
        // arrange
        when(clinicRepository.findById(1)).thenReturn(Optional.of(clinic));
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        Clinic recusada = clinicService.deny(1);

        // assert
        assertEquals(ClinicStatus.DENIED, recusada.getClinicStatus());
    }

    @Test
    public void deveDesativarClinicaEDefinirAviso() {
        // arrange
        when(clinicRepository.findById(1)).thenReturn(Optional.of(clinic));
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        Clinic desativada = clinicService.disable(1);

        // assert
        assertEquals(ClinicStatus.REMOVED, desativada.getClinicStatus());
        assertNotNull(desativada.getNotice());
    }

    @Test
    public void deveLancarExcecaoAoBuscarClinicaComEmailInexistente() {
        // arrange
        when(clinicRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class,
                () -> clinicService.findByEmail("naoexiste@email.com"));
    }

    @Test
    public void deveListarApenasClinicasPendentes() {
        // arrange
        when(clinicRepository.findByClinicStatus(ClinicStatus.PENDING)).thenReturn(List.of(clinic));

        // act
        List<Clinic> pendentes = clinicService.findPending();

        // assert
        assertEquals(1, pendentes.size());
        assertEquals(ClinicStatus.PENDING, pendentes.get(0).getClinicStatus());
    }
}