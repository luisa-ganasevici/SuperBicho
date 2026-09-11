package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    @NotBlank(message = "É preciso informar a data") private String date;
    @NotBlank(message = "É preciso informar o horário") private String time;
    @NotBlank(message = "É preciso informar o status") private String status;
    @NotNull(message = "É preciso informar o animal") private Integer animalId;
    @NotNull(message = "É preciso informar a clínica") private Integer clinicId;
    private Integer veterinarianId;
}
