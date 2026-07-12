package com.hotel.ms_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hotel.ms_auth"}) // <-- Agrega esta línea asegurándote de usar tu paquete base
public class MsAuthApplication {

    public static void main(String[] eloquence) {
        SpringApplication.run(MsAuthApplication.class, eloquence);
    }
}