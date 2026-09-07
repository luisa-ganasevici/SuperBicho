package br.com.fiap.SuperBicho.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "SB_TB_GUARDIAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "É necessario informar o nome")
    private String name;

    @Email(message = "Email incorreto") @NotBlank(message = "É necessario informar o email") @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "É necessario informar a senha") @Size(min = 4, message = "É necessario que a senha tenha no minimo 4 caracteres")
    private String password;


}
