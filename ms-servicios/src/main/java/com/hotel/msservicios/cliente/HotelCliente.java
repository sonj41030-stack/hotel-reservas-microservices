package com.hotel.msservicios.cliente;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-hoteles")
public interface HotelCliente {

    @GetMapping("/api/hoteles/{id}")
    Object obtenerHotelPorId(@PathVariable("id") Long id);
}