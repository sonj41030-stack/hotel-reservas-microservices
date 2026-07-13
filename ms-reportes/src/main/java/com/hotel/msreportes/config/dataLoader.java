package com.hotel.msreportes.config;

import com.hotel.msreportes.model.Reporte;
import com.hotel.msreportes.model.TiposReporte;
import com.hotel.msreportes.repository.ReporteRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Profile("dev")
@Component
public class dataLoader implements CommandLineRunner {

    @Autowired
    private ReporteRepository reporteRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        TiposReporte[] tipos = TiposReporte.values();

        for (int i = 0; i < 20; i++) {
            Reporte reporte = new Reporte();

            reporte.setTipo(tipos[random.nextInt(tipos.length)]);

            LocalDate fechaInicio = LocalDate.now().minusDays(random.nextInt(60) + 1);
            LocalDate fechaFin = fechaInicio.plusDays(random.nextInt(30) + 1);
            reporte.setFechaInicio(fechaInicio);
            reporte.setFechaFin(fechaFin);

            reporte.setTotalReservas(faker.number().numberBetween(1, 200));
            reporte.setTotalIngresos(faker.number().randomDouble(2, 100000, 9000000));
            reporte.setPorcentajeOcupacion(faker.number().randomDouble(2, 0, 100));
            reporte.setHotelId((long) faker.number().numberBetween(1, 5));
            reporte.setObservaciones(faker.lorem().sentence());

            reporteRepository.save(reporte);
        }
    }
}