package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GuardianResponseDTO {
    private Integer id;
    private String name;
    private String email;
}