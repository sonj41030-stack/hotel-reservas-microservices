package com.hotel.mshabitaciones.cliente;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
public class HotelCliente {

    private final WebClient webClient;

    // La URL ya NO queda fija en el código: se toma de application.properties
    // (local -> http://localhost:8085, docker -> http://ms-hoteles:8085)
    public HotelCliente(WebClient.Builder builder,
                         @Value("${hotel.service.url}") String hotelServiceUrl) {
        this.webClient = builder
                .baseUrl(hotelServiceUrl)
                .build();
    }

    /**
     * Consulta el detalle del hotel. Devuelve null si no existe o si el
     * servicio remoto no responde (no debe tumbar a ms-habitaciones).
     */
    public Object obtenerHotelPorId(Long hotelId) {
        log.info("Obteniendo hotel por id {}", hotelId);

        try {
            return webClient.get()
                    .uri("/api/hoteles/{hotelId}", hotelId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Hotel con id {} no existe en ms-hoteles", hotelId);
            return null;
        } catch (Exception e) {
            log.error("Error al consultar hotel {}: {}", hotelId, e.getMessage());
            return null;
        }
    }

    /**
     * Valida si un hotelId existe realmente en ms-hoteles antes de
     * usarlo para crear/actualizar una habitación.
     */
    public boolean existeHotel(Long hotelId) {
        log.info("Validando existencia de hotel id {}", hotelId);

        try {
            webClient.get()
                    .uri("/api/hoteles/{hotelId}", hotelId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;

        } catch (WebClientResponseException.NotFound e) {
            log.warn("Hotel con id {} no existe en ms-hoteles", hotelId);
            return false;
        } catch (Exception e) {
            log.error("No se pudo validar el hotel {} (ms-hoteles no disponible): {}",
                    hotelId, e.getMessage());
            // Fail-safe: si el servicio remoto está caído, no bloqueamos
            // la operación local, pero queda registrado en logs.
            return true;
        }
    }
}
