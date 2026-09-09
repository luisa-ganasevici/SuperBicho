package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDTO {
    private Integer id;
    private String description;
    private String type;
    private String recordDate;
    private Integer animalId;
}