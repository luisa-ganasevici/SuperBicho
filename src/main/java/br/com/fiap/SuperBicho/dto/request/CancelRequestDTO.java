package br.com.fiap.SuperBicho.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequestDTO {
    @NotBlank(message = "É necessário informar o motivo do cancelamento")
    private String reason;
}
