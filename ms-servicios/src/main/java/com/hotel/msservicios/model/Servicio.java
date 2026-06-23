package com.hotel.msservicios.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(nullable = false)
    private String descripcion;

    @DecimalMin(value = "1000.0", message = "El precio mínimo es $1.000")
    @Column(nullable = false)
    private double precio;

    @NotBlank(message = "El tipo no puede estar vacío")
    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private boolean disponible = true;

    @Column(nullable = false)
    private boolean activo = true;

}
