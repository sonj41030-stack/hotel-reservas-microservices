package com.hotel.mshabitaciones.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class habitacionResponseDTO {

    private Long id;
    private Long hotelId;
    private String tipo;
    private int capacidad;
    private double precio;
    private boolean disponible;
    private boolean permiteMascotas;
    private boolean activo;


}
