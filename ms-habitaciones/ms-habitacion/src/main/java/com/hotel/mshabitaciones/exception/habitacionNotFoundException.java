package com.hotel.mshabitaciones.exception;

public class habitacionNotFoundException extends RuntimeException {

    public habitacionNotFoundException(String mensaje) {
        super(mensaje);
    }

}
