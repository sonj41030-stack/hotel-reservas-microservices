package com.hotel.msnotificaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificacionRequestDTO {

    @NotNull(message = "clienteId es obligatorio")
    private Long clienteId;

    // Opcional: puede ir ligada a una reserva
    private Long reservaId;

    @NotBlank(message = "tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "mensaje es obligatorio")
    private String mensaje;

    @NotBlank(message = "emailDestino es obligatorio")
    @Email(message = "El formato del email es inválido")
    private String emailDestino;
}