package com.hotel.msreportes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.flyway.enabled=false",
        "eureka.client.enabled=false"
})
class MsReportesApplicationTests {

    @Test
    void contextLoads() {
    }
}