package com.hotel.mshabitaciones.cliente;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class HotelCliente {

    private final WebClient webClient;

    public HotelCliente(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8085")
                .build();
    }

    public Object obtenerHabitacionPorId(Long hotelId) {
        log.info("Obteniendo habitacion por id {}", hotelId);
        try {
            return webClient.get()
                    .uri("/api/hoteles/{hotelId}", hotelId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        }catch (Exception e){
            log.error("Error al consultar hotel {}: {}", hotelId, e.getMessage());
            return null;
        }
    }
}
