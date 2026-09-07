package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.entity.Adm;
import br.com.fiap.SuperBicho.repository.AdmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdmService {

    private final AdmRepository admRepository;
    private final PasswordEncoder passwordEncoder;

    public Adm create(Adm adm) {
        adm.setPassword(passwordEncoder.encode(adm.getPassword()));
        return admRepository.save(adm);
    }

    public Adm findById(Integer id) {
        return admRepository.findById(id).orElseThrow(() -> notFound());
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Adm not found");
    }
}