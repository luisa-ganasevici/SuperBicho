package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.*;
import br.com.fiap.SuperBicho.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/login") @RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping public LoginResponse login(@Valid @RequestBody LoginDTO loginDTO) {
        return loginService.authenticate(loginDTO); }

}
