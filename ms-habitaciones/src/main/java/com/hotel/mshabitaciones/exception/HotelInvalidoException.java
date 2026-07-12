package com.hotel.mshabitaciones.exception;

/**
 * Se lanza cuando ms-habitaciones consulta a ms-hoteles (vía WebClient)
 * y el hotelId indicado no existe o el servicio remoto no responde.
 */
public class HotelInvalidoException extends RuntimeException {

    public HotelInvalidoException(String mensaje) {
        super(mensaje);
    }
}
