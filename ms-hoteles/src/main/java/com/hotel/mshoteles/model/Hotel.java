package com.hotel.mshoteles.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hoteles")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingrese el nombre")
    @Size(min = 3, max = 100, message = "Se acepta de 3 a 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "No puede ingresar sin dirección")
    @Column(nullable = false)
    private String direccion;

    @NotBlank(message = "Campo obligatorio")
    @Column(nullable = false)
    private String ciudad;

    @NotBlank(message = "Debe ingresar el país")
    @Column(nullable = false)
    private String pais;

    @Min(value = 1, message = "Las estrellas deben ser mínimo 1")
    @Max(value = 5, message = "Las estrellas deben ser máximo 5")
    @Column(nullable = false)
    private int estrellas;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String telefono;

    @Email(message = "Ingrese un correo válido")
    @NotBlank(message = "El correo no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private boolean activo = true;
}