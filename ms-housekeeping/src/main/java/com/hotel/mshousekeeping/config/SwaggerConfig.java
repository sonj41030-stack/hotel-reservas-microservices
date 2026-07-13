package com.hotel.mshousekeeping.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Hotel - Housekeeping")
                        .version("1.0")
                        .description("Documentación de la API para el microservicio de Housekeeping. " +
                                "Gestiona las tareas de limpieza y mantenimiento de habitaciones."));
    }
}