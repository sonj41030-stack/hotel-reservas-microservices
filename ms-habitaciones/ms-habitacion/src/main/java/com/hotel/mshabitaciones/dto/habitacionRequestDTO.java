package com.hotel.mshabitaciones.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class habitacionRequestDTO {

    @NotBlank(message = "El hotelId es obligatorio")
    private Long hotelId;

    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipo;

    @Min(value = 1, message = "Mínimo 1 persona")
    private int capacidad;

    @DecimalMin(value = "35000.0", message = "El precio mínimo es $35.000")
    private double precio;

    private boolean disponible;

    private boolean permiteMascotas;
}
