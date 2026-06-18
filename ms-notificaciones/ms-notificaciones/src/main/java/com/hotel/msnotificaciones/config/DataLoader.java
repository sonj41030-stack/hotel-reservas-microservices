package com.hotel.msnotificaciones.config;

import com.hotel.msnotificaciones.model.EstadoNotificacion;
import com.hotel.msnotificaciones.model.Notificacion;
import com.hotel.msnotificaciones.repository.NotificacionRepository;
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
    private NotificacionRepository notificacionRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        String[] tipos = {
                "CONFIRMACION_RESERVA", "RECORDATORIO",
                "ALERTA_SISTEMA", "CANCELACION"
        };
        EstadoNotificacion[] estados = EstadoNotificacion.values();

        for (int i = 0; i < 20; i++) {
            Notificacion notificacion = new Notificacion();

            notificacion.setClienteId((long) faker.number().numberBetween(1, 50));
            notificacion.setReservaId((long) faker.number().numberBetween(1, 100));
            notificacion.setTipo(tipos[random.nextInt(tipos.length)]);
            notificacion.setMensaje(faker.lorem().sentence(10));
            notificacion.setEmailDestino(faker.internet().emailAddress());
            notificacion.setEstado(estados[random.nextInt(estados.length)]);
            notificacion.setFechaCreacion(LocalDateTime.now().minusDays(random.nextInt(30)));

            // fechaEnvio solo si el estado es ENVIADO
            if (notificacion.getEstado() == EstadoNotificacion.ENVIADA) {
                notificacion.setFechaEnvio(
                        notificacion.getFechaCreacion().plusMinutes(random.nextInt(60) + 1)
                );
            }

            notificacionRepository.save(notificacion);
        }
    }
}