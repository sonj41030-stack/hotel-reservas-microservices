package com.hotel.mshoteles.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class hotelResponseDTO {

    private Long id;
    private String nombre;
    private String direccion;
    private String cuidad;
    private String pais;
    private int estrellas;
    private String telefono;
    private String correo;
    private boolean activo;

}
