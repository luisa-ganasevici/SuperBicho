package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SB_TB_VACCINE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String species;

    private String description;

    @Column(name = "first_dose_age_months")
    private Integer firstDoseAgeMonths;

    @Column(name = "booster_interval_months")
    private Integer boosterIntervalMonths;
}