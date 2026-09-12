package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VaccineResponseDTO {
    private Integer id;
    private String name;
    private String species;
    private String description;
    private Integer firstDoseAgeMonths;
    private Integer boosterIntervalMonths;
}