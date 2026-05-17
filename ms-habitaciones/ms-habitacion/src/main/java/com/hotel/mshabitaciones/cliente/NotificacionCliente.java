package com.hotel.mshabitaciones.cliente;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class NotificacionCliente {

    private final WebClient webClient;

    public NotificacionCliente(WebClient.Builder  builer){
        this.webClient = builer
                .baseUrl("http://localhost:8088")
                .build();
    }
    public void enviarNotificacion(Long habitacionId, String mensaje) {
        log.info("Enviando notificacion para habitacion {}", habitacionId);
        try {
            webClient.post()
                    .uri("/notificaciones")
                    .bodyValue(new java.util.HashMap<String, Object>() {{
                        put("habitacionId", habitacionId);
                        put("mensaje", mensaje);
                    }})
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al enviar notificacion: {}", e.getMessage());
        }
    }
}
