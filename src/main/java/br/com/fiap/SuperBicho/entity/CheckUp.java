package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "SB_TB_CHECK_UP")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CheckUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    @NotBlank(message = "É necessario informar o tipo de exame ")
    private String checkUpType;

    @NotBlank(message = "É necessario informar a data do exame")
    private String checkUpDate;

    @NotBlank(message = "É necessario informar o status")
    private String status;

    @NotBlank(message = "É necessario informar as observações")
    private String notes;

    @ManyToOne @JoinColumn(name = "animal_id")
    private Animal animal;
}
