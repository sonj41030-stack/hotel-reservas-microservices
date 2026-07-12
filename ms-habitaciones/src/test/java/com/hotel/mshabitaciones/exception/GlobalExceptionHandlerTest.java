package com.hotel.mshabitaciones.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler - pruebas unitarias")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleHabitacionNotFound() debe retornar 404 con el mensaje de la excepción")
    void handleHabitacionNotFound_debeRetornar404() {
        ResponseEntity<Map<String, String>> response =
                handler.handleHabitacionNotFound(new HabitacionNotFoundException("no encontrada"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("no encontrada", response.getBody().get("error"));
    }

    @Test
    @DisplayName("handleHotelInvalido() debe retornar 400 con el mensaje de la excepción")
    void handleHotelInvalido_debeRetornar400() {
        ResponseEntity<Map<String, String>> response =
                handler.handleHotelInvalido(new HotelInvalidoException("hotel invalido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("hotel invalido", response.getBody().get("error"));
    }

    @Test
    @DisplayName("handleIllegalArgument() debe retornar 400 con el mensaje de la excepción")
    void handleIllegalArgument_debeRetornar400() {
        ResponseEntity<Map<String, String>> response =
                handler.handleIllegalArgument(new IllegalArgumentException("argumento invalido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("argumento invalido", response.getBody().get("error"));
    }

    @Test
    @DisplayName("handleGeneral() debe retornar 500 con mensaje genérico")
    void handleGeneral_debeRetornar500() {
        ResponseEntity<Map<String, String>> response =
                handler.handleGeneral(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrió un error inesperado", response.getBody().get("error"));
    }
}
