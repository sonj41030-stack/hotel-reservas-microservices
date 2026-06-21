package com.hotel.mshoteles.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class hotelRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "Campo obligatorio")
    private String direccion;

    @NotBlank(message = "Campo obligatorio")
    private String cuidad;

    @NotBlank(message = "El país no puede estar vacío")
    private String pais;

    @Min(value = 1, message = "minimo 1 estrella")
    @Max(value = 5, message = "Maximo 5 estrellas")
    private int estrellas;

    @NotBlank(message = "Ingrese un telefono, campo obligatorio")
    private String telefono;


    @Email(message = "El correo debe ser valido")
    @NotBlank(message = "Campo obligatorio")
    private String correo ;

}
