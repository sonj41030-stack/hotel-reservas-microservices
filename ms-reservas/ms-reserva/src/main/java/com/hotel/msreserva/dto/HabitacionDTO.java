package com.hotel.msreserva.dto;

import lombok.Data;

@Data
public class HabitacionDTO {
    private Long id;
    private String tipo;
    private Double precio;
    private Boolean disponible;
    private Boolean permiteMascotas;
    private Long hotelId;
}