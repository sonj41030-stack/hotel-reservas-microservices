package com.hotel.msnotificaciones.dto;

import com.hotel.msnotificaciones.model.EstadoNotificacion;
import lombok.Data;

import java.time.LocalDateTime;

// Este DTO es lo que retornamos al cliente — nunca exponemos la entidad directamente
@Data
public class NotificacionResponseDTO {

    private Long id;
    private Long clienteId;
    private Long reservaId;
    private String tipo;
    private String mensaje;
    private String emailDestino;
    private EstadoNotificacion estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
}