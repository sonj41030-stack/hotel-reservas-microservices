package com.hotel.msclientes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel BDI - ms-clientes")
                        .version("1.0")
                        .description("Microservicio para gestionar clientes del hotel")
                        .contact(new Contact()
                                .name("Jhonaiker")
                                .email("sonj41030@gmail.com")));
    }
}