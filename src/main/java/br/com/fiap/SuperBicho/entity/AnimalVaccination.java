package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "SB_TB_ANIMAL_VACCINATION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnimalVaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "É necessário informar a data de aplicação")
    @Column(name = "application_date")
    private String applicationDate;

    @ManyToOne @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;
}