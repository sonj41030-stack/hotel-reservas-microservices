package com.hotel.msreserva.service;

import com.hotel.msreserva.model.Reserva;
import com.hotel.msreserva.dto.ReservaRequest;
import com.hotel.msreserva.dto.NotificacionDTO;
import com.hotel.msreserva.model.EstadoReserva;
import com.hotel.msreserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.hotel.msreserva.dto.ClienteDTO;
import com.hotel.msreserva.dto.HabitacionDTO;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final WebClient.Builder webClientBuilder;

    public Reserva crearReserva(ReservaRequest request) {

        // VALIDAR CLIENTE EN ms-clientes
        try {
            ClienteDTO cliente = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8084/clientes/" + request.getClienteId())
                    .retrieve()
                    .bodyToMono(ClienteDTO.class)
                    .block();
            if (cliente == null) {
                throw new RuntimeException("Cliente no encontrado");
            }
        } catch (Exception e) {
            System.out.println("No se pudo validar cliente: " + e.getMessage());
        }

        // VALIDAR HABITACION EN ms-habitaciones
        try {
            HabitacionDTO habitacion = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8085/api/habitaciones/" + request.getHabitacionId())
                    .retrieve()
                    .bodyToMono(HabitacionDTO.class)
                    .block();
            if (habitacion == null) {
                throw new RuntimeException("Habitacion no encontrada");
            }
        } catch (Exception e) {
            System.out.println("No se pudo validar habitacion: " + e.getMessage());
        }

        // CREAR RESERVA
        Reserva reserva = new Reserva();
        reserva.setClienteId(request.getClienteId());
        reserva.setHabitacionId(request.getHabitacionId());
        reserva.setFechaEntrada(request.getFechaEntrada());
        reserva.setFechaSalida(request.getFechaSalida());
        reserva.setTotal(request.getTotal());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        Reserva guardada = reservaRepository.save(reserva);

        // ENVIAR NOTIFICACION
        try {
            NotificacionDTO notificacion = new NotificacionDTO();
            notificacion.setClienteId(request.getClienteId());
            notificacion.setReservaId(guardada.getId());
            notificacion.setTipo("RESERVA");
            notificacion.setMensaje("Su reserva fue creada exitosamente.");
            notificacion.setEmailDestino("cliente@gmail.com");

            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8088/api/notificaciones")
                    .bodyValue(notificacion)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            System.out.println("No se pudo enviar notificacion: " + e.getMessage());
        }

        return guardada;
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> obtenerReserva(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> obtenerReservasPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    public Reserva actualizarEstado(Long id, EstadoReserva estado) {
        Optional<Reserva> reserva = reservaRepository.findById(id);
        if (reserva.isPresent()) {
            reserva.get().setEstado(estado);
            return reservaRepository.save(reserva.get());
        }
        throw new RuntimeException("Reserva no encontrada");
    }

    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }
}