package com.hotel.mshousekeeping.config;

import com.hotel.mshousekeeping.model.EstadoTarea;
import com.hotel.mshousekeeping.model.TareaHousekeeping;
import com.hotel.mshousekeeping.repository.TareaRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TareaRepository tareaRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        String[] tiposTarea = {"LIMPIEZA", "CHECK_IN", "CHECK_OUT", "MANTENIMIENTO"};
        String[] prioridades = {"BAJA", "MEDIA", "ALTA"};
        EstadoTarea[] estados = EstadoTarea.values();

        for (int i = 0; i < 20; i++) {
            TareaHousekeeping tarea = new TareaHousekeeping();

            tarea.setHabitacionId((long) faker.number().numberBetween(1, 50));
            tarea.setTipoTarea(tiposTarea[random.nextInt(tiposTarea.length)]);
            tarea.setEstado(estados[random.nextInt(estados.length)]);
            tarea.setEmpleadoAsignado(faker.name().fullName());
            tarea.setPrioridad(prioridades[random.nextInt(prioridades.length)]);
            tarea.setObservaciones(faker.lorem().sentence());
            tarea.setFechaAsignacion(LocalDateTime.now().minusDays(random.nextInt(30)));

            if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                tarea.setFechaCompletado(tarea.getFechaAsignacion().plusHours(random.nextInt(8) + 1));
            }

            tareaRepository.save(tarea);
        }
    }
}