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
    @NotBlank(message = "É necessario informar as observações") private String notes;
    @NotNull(message = "É preciso informar o animal") private Integer animalId;
}