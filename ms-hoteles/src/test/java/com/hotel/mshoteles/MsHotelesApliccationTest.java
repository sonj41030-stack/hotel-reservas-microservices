package com.hotel.mshoteles;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MsHotelesApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot levante correctamente:
        // beans, JPA/Hibernate, WebClient, controllers, etc.
    }

}