package com.hotel.mshoteles.exception;

public class hotelNotFoundException extends RuntimeException{

    public hotelNotFoundException(String mensaje){
        super(mensaje);
    }
}
