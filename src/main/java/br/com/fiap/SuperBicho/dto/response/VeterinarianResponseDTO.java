package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianResponseDTO {
    private Integer id;
    private String name;
    private String specialty;
    private Integer clinicId;
    private String clinicName;
}