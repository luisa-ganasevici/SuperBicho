package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Integer id;
    private String date;
    private String time;
    private String status;
    private Integer animalId;
    private String animalName;
    private Integer clinicId;
    private String clinicName;
}