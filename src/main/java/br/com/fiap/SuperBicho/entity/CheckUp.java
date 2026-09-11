package br.com.fiap.SuperBicho.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "SB_TB_CHECK_UP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "cancel_reason")
    private String cancelReason;

    @ManyToOne @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @ManyToOne @JoinColumn(name = "veterinarian_id")
    private Veterinarian veterinarian;
}
