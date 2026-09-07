package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "SB_TB_APPOINTMENT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "É preciso informar a data")
    private String date;

    @NotBlank(message = "É preciso informar horario")
    private String time;

    @NotBlank(message = "É preciso informar o status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;
}
