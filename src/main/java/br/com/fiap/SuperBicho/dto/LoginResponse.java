package br.com.fiap.SuperBicho.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {

    private Integer id;

    private String name;

    private String email;

    private String userType;

}
