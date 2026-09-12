package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnimalVaccinationRequestDTO {
    @NotNull(message = "É preciso informar o animal") private Integer animalId;
    @NotNull(message = "É preciso informar a vacina") private Integer vaccineId;
    @NotBlank(message = "É preciso informar a data de aplicação") private String applicationDate;
}