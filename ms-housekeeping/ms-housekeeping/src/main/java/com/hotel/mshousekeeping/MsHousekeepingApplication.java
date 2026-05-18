package com.hotel.mshousekeeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsHousekeepingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsHousekeepingApplication.class, args);
    }
}