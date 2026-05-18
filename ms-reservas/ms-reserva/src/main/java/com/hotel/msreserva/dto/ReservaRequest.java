package com.hotel.msreserva.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaRequest {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long  habitacionId;

    @NotNull
    private LocalDate fechaEntrada;

    @NotNull
    private LocalDate fechaSalida;

    private Double total;
}