package com.hotel.msclientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String mensaje;
    private int codigoEstado;
    private long timestamp;
    private Map<String, String> erroresValidacion; // Opcional, para validaciones

    public ErrorResponse(String mensaje, int codigoEstado) {
        this.mensaje = mensaje;
        this.codigoEstado = codigoEstado;
        this.timestamp = System.currentTimeMillis();
    }
}