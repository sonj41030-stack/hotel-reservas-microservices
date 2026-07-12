package com.hotel.msreportes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name = spring.application.name de ms-hoteles
// url  = fallback directo si Eureka no está activo
@FeignClient(name = "ms-hoteles", url = "http://localhost:8085")
public interface HotelClient {

    @GetMapping("/api/hoteles/{id}")
    Object obtenerHotel(@PathVariable Long id);
}