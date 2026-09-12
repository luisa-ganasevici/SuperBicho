package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SeasonAlertResponseDTO {
    private String title;
    private String message;
    private String linkUrl;
    private String linkLabel;
}