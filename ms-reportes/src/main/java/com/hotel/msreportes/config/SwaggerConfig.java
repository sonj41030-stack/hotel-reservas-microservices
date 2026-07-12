package com.hotel.msreportes.config;

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
                        .title("API Hotel - Reportes")
                        .version("1.0")
                        .description("Documentación de la API para el microservicio de Reportes. " +
                                "Gestiona la generación y consulta de reportes de ocupación, " +
                                "ingresos y reservas del hotel."));
    }
}