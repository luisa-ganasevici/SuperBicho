package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "SB_TB_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "É necessario informar a descrição")
    private String description;

    @NotBlank(message = "É necessario informar o tipo")
    private String type;

    @NotBlank(message = "É necessario informar a data de registro")
    private String recordDate;

    @ManyToOne @JoinColumn(name = "animal_id")
    private Animal animal;
}
