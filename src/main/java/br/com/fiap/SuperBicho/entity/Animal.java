package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "SB_TB_ANIMAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "É preciso informar o nome do animal")
    private String name;

    @NotBlank(message = "É preciso informar a especie")
    private String species;

    @NotNull(message = "É preciso informar a idade") @Min(value = 0, message = "A idade precisa ser maior que zero")
    private Integer age;

    @NotNull(message = "É preciso informar o peso") @Positive(message = "Peso deve ser maior que zero")
    private Double weight;

    @ManyToOne @JoinColumn(name = "guardian_id")
    private Guardian guardian;
}
