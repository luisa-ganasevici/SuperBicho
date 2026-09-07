package br.com.fiap.SuperBicho.entity;


import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "SB_TB_ADM")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Adm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Email(message = "Email inválido")
    @NotBlank(message = "É necessário informar o email")
    private String email;

    @NotBlank(message = "É necessario informar a senha")
    private String password;


}
