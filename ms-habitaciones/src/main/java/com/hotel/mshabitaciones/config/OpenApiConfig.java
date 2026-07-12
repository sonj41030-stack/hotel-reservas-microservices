package com.hotel.mshabitaciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI habitacionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Habitaciones API")
                        .description("Microservicio encargado de la gestión de habitaciones, " +
                                "validando la existencia del hotel asociado contra ms-hoteles.")
                        .version("v1.0"));
    }
}
