package com.hotel.mshousekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TareaDTO {

    @NotNull(message = "habitacionId es obligatorio")
    private Long habitacionId;

    @NotBlank(message = "tipoTarea es obligatorio")
    @Pattern(
            regexp = "LIMPIEZA|CHECK_IN|CHECK_OUT|MANTENIMIENTO",
            message = "tipoTarea debe ser: LIMPIEZA, CHECK_IN, CHECK_OUT o MANTENIMIENTO"
    )
    private String tipoTarea;

    private String empleadoAsignado;

    @Pattern(
            regexp = "BAJA|MEDIA|ALTA",
            message = "prioridad debe ser: BAJA, MEDIA o ALTA"
    )
    private String prioridad;

    private String observaciones;
}