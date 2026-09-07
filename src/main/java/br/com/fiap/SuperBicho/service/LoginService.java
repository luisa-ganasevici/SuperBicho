package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.*;
import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.exception.UserNotFoundException;
import br.com.fiap.SuperBicho.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class LoginService {

    private final GuardianRepository guardianRepository;

    public LoginResponse authenticate(LoginDTO loginDTO) {

        Guardian guardian = guardianRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()
                -> new UserNotFoundException("Email or password is incorrect"));

        if (!guardian.getPassword().equals(loginDTO.getPassword()))
            throw new UserNotFoundException("Email or password is incorrect");

        return new LoginResponse(guardian.getId(), guardian.getName(), guardian.getEmail());
    }
}
