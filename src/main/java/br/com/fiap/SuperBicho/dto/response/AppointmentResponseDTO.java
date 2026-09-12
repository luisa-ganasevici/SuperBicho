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
    private String cancelReason;
    private Integer animalId;
    private String animalName;
    private Integer clinicId;
    private String clinicName;
    private Integer veterinarianId;
    private String veterinarianName;
    private String guardianName;
}
