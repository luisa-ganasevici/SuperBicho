package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.entity.Adm;
import br.com.fiap.SuperBicho.repository.AdmRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdmServiceTest {

    @Mock
    private AdmRepository admRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdmService admService;

    private Adm adm;

    @BeforeEach
    public void arrange() {
        adm = new Adm(1, "adm@superbicho.com", "senhaCriptografada");
    }

    @Test
    public void deveCadastrarAdmComSenhaCriptografada() {
        // arrange
        Adm novoAdm = new Adm(null, "adm@superbicho.com", "senha123");
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
        when(admRepository.save(novoAdm)).thenReturn(adm);

        // act
        Adm salvo = admService.create(novoAdm);

        // assert
        assertEquals("senhaCriptografada", novoAdm.getPassword());
        assertEquals(1, salvo.getId());
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    public void deveBuscarAdmPorIdComSucesso() {
        // arrange
        when(admRepository.findById(1)).thenReturn(Optional.of(adm));

        // act
        Adm encontrado = admService.findById(1);

        // assert
        assertEquals("adm@superbicho.com", encontrado.getEmail());
    }

    @Test
    public void deveLancarExcecaoAoBuscarAdmInexistente() {
        // arrange
        when(admRepository.findById(99)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ResponseStatusException.class, () -> admService.findById(99));
    }
}