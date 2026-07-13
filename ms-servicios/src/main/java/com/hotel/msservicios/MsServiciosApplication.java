package com.hotel.msservicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsServiciosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsServiciosApplication.class, args);
    }

}
