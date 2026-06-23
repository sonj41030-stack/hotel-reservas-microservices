package com.hotel.mshabitaciones.cliente;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class NotificacionCliente {

    private final WebClient webClient;

    public NotificacionCliente(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8088")
                .build();
    }

    public void enviarNotificacion(Long habitacionId, String mensaje) {

        log.info("Enviando notificacion para habitacion {}", habitacionId);

        try {

            Map<String, Object> body = new HashMap<>();
            body.put("habitacionId", habitacionId);
            body.put("mensaje", mensaje);

            webClient.post()
                    .uri("/notificaciones")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

        } catch (Exception e) {
            log.error("Error al enviar notificacion: {}", e.getMessage());
        }
    }
}