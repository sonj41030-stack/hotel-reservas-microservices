package com.hotel.msservicios.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String tipo;
    private boolean disponible;
    private boolean activo;
}
