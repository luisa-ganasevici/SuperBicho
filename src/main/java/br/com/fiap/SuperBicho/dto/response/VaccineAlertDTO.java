package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class VaccineAlertDTO {
    private Integer animalId;
    private String animalName;
    private String vaccineName;
    private String nextDueDate;
    private String calendarStatus;
}