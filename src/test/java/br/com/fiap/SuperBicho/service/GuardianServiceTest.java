package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.GuardianRequestDTO;
import br.com.fiap.SuperBicho.dto.response.GuardianResponseDTO;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.repository.GuardianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuardianServiceTest {

    @Mock
    private GuardianRepository guardianRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private GuardianService guardianService;

    private Guardian guardian;
    private GuardianRequestDTO guardianRequestDTO;

    @BeforeEach
    public void arrange() {
        guardian = new Guardian();
        guardian.setId(1);
        guardian.setName("Maria Silva");
        guardian.setEmail("maria@email.com");
        guardian.setPassword("senhaCriptografada");

        guardianRequestDTO = new GuardianRequestDTO("Maria Silva", "maria@email.com", "senha123");
    }

    @Test
    public void deveCadastrarGuardianComSenhaCriptografada() {
        // arrange
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(guardianRepository.save(any(Guardian.class))).thenReturn(guardian);

        // act
        GuardianResponseDTO response = guardianService.create(guardianRequestDTO);

        // assert
        assertEquals("Maria Silva", response.getName());
        assertEquals("maria@email.com", response.getEmail());
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    public void deveBuscarGuardianPorEmailComSucesso() {
        // arrange
        when(guardianRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(guardian));

        // act
        Guardian encontrado = guardianService.findByEmail("maria@email.com");

        // assert
        assertEquals("maria@email.com", encontrado.getEmail());
    }

    @Test
    public void deveLancarExcecaoAoBuscarGuardianComEmailInexistente() {
        // arrange
        when(guardianRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class,
                () -> guardianService.findByEmail("naoexiste@email.com"));
    }

    @Test
    public void deveAtualizarGuardianComSucesso() {
        // arrange
        GuardianRequestDTO atualizacao = new GuardianRequestDTO("Maria Souza", "maria@email.com", "novaSenha");
        when(guardianRepository.findById(1)).thenReturn(Optional.of(guardian));
        when(passwordEncoder.encode("novaSenha")).thenReturn("novaSenhaCriptografada");
        when(guardianRepository.save(any(Guardian.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        GuardianResponseDTO response = guardianService.update(1, atualizacao);

        // assert
        assertEquals("Maria Souza", response.getName());
        verify(passwordEncoder, times(1)).encode("novaSenha");
    }

    @Test
    public void deveLancarExcecaoAoExcluirGuardianInexistente() {
        // arrange
        when(guardianRepository.existsById(99)).thenReturn(false);

        // act + assert
        assertThrows(ResponseStatusException.class, () -> guardianService.deleteById(99));
        verify(guardianRepository, never()).deleteById(any());
    }
}