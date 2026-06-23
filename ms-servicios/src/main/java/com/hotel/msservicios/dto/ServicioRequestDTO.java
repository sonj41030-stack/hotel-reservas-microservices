package com.hotel.msservicios.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @DecimalMin(value = "1000.0", message = "El precio mínimo es $1.000")
    private double precio;

    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipo;

    private boolean disponible;

}
