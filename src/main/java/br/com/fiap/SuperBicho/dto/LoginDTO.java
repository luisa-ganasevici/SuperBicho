package br.com.fiap.SuperBicho.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginDTO {

    @Email(message = "Email inválido") @NotBlank(message = "É preciso informar um email") private String email;

    @NotBlank(message = "A senha é necessária") private String password;
}
