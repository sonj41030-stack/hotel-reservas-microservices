package com.hotel.msservicios.exception;

/**
 * Se lanza cuando ms-servicios consulta a ms-hoteles (vía Feign Client)
 * y el hotelId indicado no existe o el servicio remoto no responde.
 */
public class HotelInvalidoException extends RuntimeException {

    public HotelInvalidoException(String mensaje) {
        super(mensaje);
    }
}
