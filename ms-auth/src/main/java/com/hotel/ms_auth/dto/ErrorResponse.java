package com.hotel.ms_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String mensaje;
    private int codigoEstado;
    private long timestamp;
    private Map<String, String> erroresValidacion; // Para capturar campos vacíos o malos en el registro

    // Constructor corto para errores generales
    public ErrorResponse(String mensaje, int codigoEstado) {
        this.mensaje = mensaje;
        this.codigoEstado = codigoEstado;
        this.timestamp = System.currentTimeMillis();
    }
}