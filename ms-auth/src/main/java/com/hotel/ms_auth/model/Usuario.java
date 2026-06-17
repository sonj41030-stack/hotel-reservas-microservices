package com.hotel.ms_auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario")
@Schema(description = "Identidad que representa nuestro Cliente")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    @NotBlank
    @Size(max = 20)
    @Schema(
            description = "RUT del Cliente",
            example = "12345678-9",
            maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED
    )

    @NotBlank
    private String nombre;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

}
