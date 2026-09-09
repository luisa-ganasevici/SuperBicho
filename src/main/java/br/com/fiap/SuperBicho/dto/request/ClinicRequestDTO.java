package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicRequestDTO {
    @NotBlank(message = "É necessario informar o nome da clinica") private String name;
    @NotBlank(message = "É necessario informar o endereço") private String address;
    @NotBlank(message = "É necessario informar o numero de telefone") private String phone;
    @Email(message = "Email inválido") @NotBlank(message = "É necessario informar o email") private String email;
    @NotBlank(message = "É necessario informar senha") private String password;

}