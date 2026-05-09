package com.hotel.mshabitaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "habitaciones")

public class Habitacion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El hotelId es obligatorio")
    @Column(nullable = false)
    private Long hotelId;

    @NotBlank(message = "El tipo no puede estar vacío")
    @Column(nullable = false)
    private String tipo;

    @Min(value = 1, message = "Mínimo 1 persona")
    @Column(nullable = false)
    private int capacidad;

    @DecimalMin(value = "35000.0", message = "El precio mínimo es de $35.000")
    @Column(nullable = false)
    private double precio;

    @Column(nullable = false)
    private boolean disponible = true;

    @Column(nullable = false)
    private boolean permiteMascotas = false;

    @Column(nullable = false)
    private boolean activo = true;

}
