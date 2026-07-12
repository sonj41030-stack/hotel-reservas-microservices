package com.hotel.mshabitaciones.cliente;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración liviana para HotelCliente.
 * Se usa com.sun.net.httpserver.HttpServer (viene incluido en el JDK,
 * no requiere agregar dependencias) para simular ms-hoteles y así
 * cubrir las tres ramas de existeHotel()/obtenerHotelPorId():
 * hotel existe (200), hotel no existe (404) y servicio caído (excepción).
 */
@DisplayName("HotelCliente - pruebas con servidor HTTP simulado")
class HotelClienteTest {

    private HttpServer servidor;
    private HotelCliente hotelCliente;
    private int puerto;

    @BeforeEach
    void setUp() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(0), 0);
        puerto = servidor.getAddress().getPort();

        servidor.createContext("/api/hoteles/1", exchange -> {
            String respuesta = "{\"id\":1,\"nombre\":\"Hotel Test\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, respuesta.getBytes().length);
            exchange.getResponseBody().write(respuesta.getBytes());
            exchange.close();
        });

        servidor.createContext("/api/hoteles/999", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });

        servidor.start();

        String baseUrl = "http://localhost:" + puerto;
        hotelCliente = new HotelCliente(WebClient.builder(), baseUrl);
    }

    @AfterEach
    void tearDown() {
        servidor.stop(0);
    }

    @Test
    @DisplayName("existeHotel() debe retornar true cuando el hotel existe (200)")
    void existeHotel_debeRetornarTrueSiExiste() {
        assertTrue(hotelCliente.existeHotel(1L));
    }

    @Test
    @DisplayName("existeHotel() debe retornar false cuando el hotel no existe (404)")
    void existeHotel_debeRetornarFalseSiNoExiste() {
        assertFalse(hotelCliente.existeHotel(999L));
    }

    @Test
    @DisplayName("existeHotel() debe retornar true (fail-safe) cuando ms-hoteles no responde")
    void existeHotel_debeRetornarTrueFailSafeSiServicioCaido() {
        // hotelId sin contexto registrado en el servidor simulado -> 404 real del HttpServer base,
        // pero para simular un servicio "caído" apuntamos a un puerto cerrado.
        servidor.stop(0);
        assertTrue(hotelCliente.existeHotel(1L));
    }

    @Test
    @DisplayName("obtenerHotelPorId() debe retornar el objeto cuando el hotel existe")
    void obtenerHotelPorId_debeRetornarObjetoSiExiste() {
        Object resultado = hotelCliente.obtenerHotelPorId(1L);
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("obtenerHotelPorId() debe retornar null cuando el hotel no existe")
    void obtenerHotelPorId_debeRetornarNullSiNoExiste() {
        assertNull(hotelCliente.obtenerHotelPorId(999L));
    }

    @Test
    @DisplayName("obtenerHotelPorId() debe retornar null cuando ms-hoteles no responde")
    void obtenerHotelPorId_debeRetornarNullSiServicioCaido() {
        servidor.stop(0);
        assertNull(hotelCliente.obtenerHotelPorId(1L));
    }
}
