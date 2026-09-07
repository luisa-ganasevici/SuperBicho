package br.com.fiap.SuperBicho.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnimalDTO {
    @NotBlank(message = "É preciso informar o nome do animal") private String name;

    @NotBlank(message = "É preciso informar a especie ") private String species;

    @NotNull(message = "É preciso informar a idade") @Min(value = 0, message = "Idade não pode ser menor que zero") private Integer age;

    @NotNull(message = "É preciso informar o peso") @Positive(message = "Peso precisa ser maior que zero") private Double weight;

    @NotNull(message = "É preciso informar o responsável") private Integer guardianId;
}
