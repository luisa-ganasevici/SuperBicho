package br.com.fiap.SuperBicho.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "SB_TB_CLINIC")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "É necessario informar o nome da clinica")
    private String name;

    @NotBlank(message = "É necessario informar o endereço")
    private String address;

    @NotBlank(message = "É necessario informar o numero de telefone")
    private String phone;

    @Email(message = "Email inválido")
    @NotBlank(message = "É necessario informar o email")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "É necessario informar senha")
    private String password;

    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    @NotBlank(message = "É necessario informar o CNPJ")
    private String cnpj;

    @ElementCollection
    @CollectionTable(name = "SB_TB_CLINIC_SPECIALTY", joinColumns = @JoinColumn(name = "clinic_id"))
    @Column(name = "specialty")
    private List<String> specialties;

    @Enumerated(EnumType.STRING)
    private ClinicStatus clinicStatus;

}
