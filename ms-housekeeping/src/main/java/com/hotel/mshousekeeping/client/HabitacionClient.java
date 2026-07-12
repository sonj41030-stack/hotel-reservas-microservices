package com.hotel.mshousekeeping.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Llama a ms-habitaciones para validar que la habitación existe
@FeignClient(name = "ms-habitaciones", url = "http://localhost:8086")
public interface HabitacionClient {

    @GetMapping("/api/habitaciones/{id}")
    Object obtenerHabitacion(@PathVariable("id") Long id);
}