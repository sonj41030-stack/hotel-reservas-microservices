package com.hotel.msreserva.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Reservas - Hotel Duoc")
                        .version("1.0")
                        .description("Documentación técnica para el microservicio de gestión de reservas, cumpliendo con la EFT DSY1103."));
    }
}