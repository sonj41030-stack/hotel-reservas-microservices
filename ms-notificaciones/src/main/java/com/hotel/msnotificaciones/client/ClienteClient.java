package com.hotel.msnotificaciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// "ms-clientes" debe coincidir con spring.application.name de ese microservicio
// url es el fallback si Eureka no está activo
@FeignClient(name = "ms-clientes", url = "http://localhost:8084")
public interface ClienteClient {

    @GetMapping("/api/clientes/{id}")
    Object verificarCliente(@PathVariable Long id);
}