package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
}
