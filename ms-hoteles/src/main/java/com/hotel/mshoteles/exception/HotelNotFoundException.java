package com.hotel.mshoteles.exception;

public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException(String mensaje) {
        super(mensaje);
    }
}
