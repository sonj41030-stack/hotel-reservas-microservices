package com.hotel.mspagos.dto;

import lombok.Data;

@Data
public class NotificacionDTO {
    private Long clienteId;
    private Long reservaId;
    private String tipo;
    private String mensaje;
    private String emailDestino;
}