package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianRequestDTO {
    @NotBlank(message = "É necessário informar o nome do veterinário") private String name;
    @NotBlank(message = "É necessário informar a especialidade") private String specialty;
    @NotNull(message = "É necessário informar a clínica") private Integer clinicId;
}