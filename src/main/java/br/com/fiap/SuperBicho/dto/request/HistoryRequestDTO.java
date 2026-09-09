package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequestDTO {
    @NotBlank(message = "É necessario informar a descrição") private String description;
    @NotBlank(message = "É necessario informar o tipo") private String type;
    @NotBlank(message = "É necessario informar a data de registro") private String recordDate;
    @NotNull(message = "É preciso informar o animal") private Integer animalId;
}