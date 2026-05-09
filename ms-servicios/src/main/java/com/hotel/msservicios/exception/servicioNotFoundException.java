package com.hotel.msservicios.exception;

public class servicioNotFoundException extends RuntimeException {
    public servicioNotFoundException(String mensaje) {
        super(mensaje);
    }
}
