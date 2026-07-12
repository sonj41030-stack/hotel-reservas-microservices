package com.hotel.mshabitaciones.cliente;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("NotificacionCliente - pruebas con servidor HTTP simulado")
class NotificacionClienteTest {

    private HttpServer servidor;
    private NotificacionCliente notificacionCliente;
    private int puerto;

    @BeforeEach
    void setUp() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(0), 0);
        puerto = servidor.getAddress().getPort();
        servidor.start();

        String baseUrl = "http://localhost:" + puerto;
        notificacionCliente = new NotificacionCliente(WebClient.builder(), baseUrl);
    }

    @AfterEach
    void tearDown() {
        servidor.stop(0);
    }

    @Test
    @DisplayName("enviarNotificacion() no debe lanzar excepción cuando ms-notificaciones responde OK")
    void enviarNotificacion_noDebeLanzarExcepcionSiServicioResponde() {
        AtomicBoolean llamadoRecibido = new AtomicBoolean(false);

        servidor.createContext("/notificaciones", exchange -> {
            llamadoRecibido.set(true);
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });

        assertDoesNotThrow(() -> notificacionCliente.enviarNotificacion(1L, "Habitación creada"));
        assertTrue(llamadoRecibido.get());
    }

    @Test
    @DisplayName("enviarNotificacion() no debe lanzar excepción cuando ms-notificaciones no responde")
    void enviarNotificacion_noDebeLanzarExcepcionSiServicioCaido() {
        servidor.stop(0);

        assertDoesNotThrow(() -> notificacionCliente.enviarNotificacion(1L, "Habitación creada"));
    }
}
