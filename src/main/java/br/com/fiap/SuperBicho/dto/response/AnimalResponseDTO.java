package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnimalResponseDTO {
    private Integer id;
    private String name;
    private String species;
    private Integer age;
    private Double weight;
    private Integer guardianId;

}