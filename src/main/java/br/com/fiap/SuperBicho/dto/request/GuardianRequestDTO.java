package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuardianRequestDTO {
    @NotBlank(message = "É necessario informar o nome") private String name;
    @Email(message = "Email incorreto") @NotBlank(message = "É necessario informar o email") private String email;
    @NotBlank(message = "É necessario informar a senha") @Size(min = 4, message = "É necessario que a senha tenha no minimo 4 caracteres") private String password;
}