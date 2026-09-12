package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnimalVaccinationResponseDTO {
    private Integer id;
    private Integer animalId;
    private Integer vaccineId;
    private String vaccineName;
    private String applicationDate;
}