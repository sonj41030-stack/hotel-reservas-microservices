package com.hotel.mshabitaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionResponseDTO {

    private Long id;
    private Long hotelId;
    private String tipo;
    private int capacidad;
    private double precio;
    private boolean disponible;
    private boolean permiteMascotas;
    private boolean activo;

}
