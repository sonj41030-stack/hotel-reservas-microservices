package com.hotel.msservicios.exception;

public class ServicioNotFoundException extends RuntimeException {
    public ServicioNotFoundException(String mensaje) {
        super(mensaje);
    }
}
