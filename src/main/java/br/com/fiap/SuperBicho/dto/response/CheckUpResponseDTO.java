package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckUpResponseDTO {
    private Integer id;
    private String checkUpType;
    private String checkUpDate;
    private String status;
    private String notes;
    private Integer animalId;
    private String animalName;
}