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
    private String cancelReason;
    private Integer animalId;
    private String animalName;
    private Integer clinicId;
    private String clinicName;
    private String guardianName;
}
