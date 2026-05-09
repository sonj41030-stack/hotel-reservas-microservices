package com.hotel.msservicios.cliente;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class hotelCliente {

    private final WebClient webClient;

    public hotelCliente(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8085")
                .build();
    }
    public Object obtenerHotelPorId(Long hotelId) {
        log.info("Consultando hotel con id: {}", hotelId);
        try {
            return webClient.get()
                    .uri("/api/hoteles/{hotelId}", hotelId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al consultar hotel {}: {}", hotelId, e.getMessage());
            return null;
        }
    }
}
