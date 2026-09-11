package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckUpRequestDTO {
    @NotBlank(message = "É necessario informar o tipo de exame") private String checkUpType;
    @NotBlank(message = "É necessario informar a data do exame") private String checkUpDate;
    @NotBlank(message = "É necessario informar o status") private String status;
    @NotNull(message = "É preciso informar o animal") private Integer animalId;
    @NotNull(message = "É preciso informar a clínica") private Integer clinicId;
    private Integer veterinarianId;
}
