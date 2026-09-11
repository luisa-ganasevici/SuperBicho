package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "SB_TB_VETERINARIAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Veterinarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "É necessário informar o nome do veterinário")
    private String name;

    @NotBlank(message = "É necessário informar a especialidade")
    private String specialty;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;
}