package br.com.fiap.SuperBicho.dto.response;

import br.com.fiap.SuperBicho.entity.ClinicStatus;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicResponseDTO {
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private ClinicStatus clinicStatus;
    private String cnpj;
    private List<String> specialties;
}