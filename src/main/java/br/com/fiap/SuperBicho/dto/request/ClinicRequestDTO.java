package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicRequestDTO {

    @NotBlank(message = "É necessario informar o nome da clinica")
    private String name;

    @NotBlank(message = "É necessario informar o endereço")
    private String address;

    @NotBlank(message = "É necessario informar o numero de telefone")
    private String phone;

    @Email(message = "Email inválido")
    @NotBlank(message = "É necessario informar o email")
    private String email;

    @NotBlank(message = "É necessario informar senha")
    private String password;

    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    @NotBlank(message = "É necessario informar o CNPJ")
    private String cnpj;
    private List<String> specialties;
}