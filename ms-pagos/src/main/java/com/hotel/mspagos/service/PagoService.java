package com.hotel.mspagos.service;

import com.hotel.mspagos.dto.NotificacionDTO;
import com.hotel.mspagos.dto.PagoRequest;
import com.hotel.mspagos.dto.PagoResponse;
import com.hotel.mspagos.model.EstadoPago;
import com.hotel.mspagos.model.Pago;
import com.hotel.mspagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final WebClient webClient;

    public PagoResponse procesarPago(PagoRequest request) {
        Pago pago = new Pago();
        pago.setReservaId(request.getReservaId());
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setEstado(EstadoPago.COMPLETADO);
        pago.setFechaPago(LocalDateTime.now());
        Pago guardado = pagoRepository.save(pago);

        // Avisar a ms-reservas que el pago fue exitoso
        try {
            webClient.put()
                    .uri("http://localhost:8082/reservas/{id}/estado?estado=CONFIRMADA",
                            request.getReservaId())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            System.out.println("No se pudo actualizar la reserva: " + e.getMessage());
        }

        // Enviar notificacion a ms-notificaciones
        try {
            NotificacionDTO notificacion = new NotificacionDTO();
            notificacion.setClienteId(1L);
            notificacion.setReservaId(request.getReservaId());
            notificacion.setTipo("PAGO");
            notificacion.setMensaje("Su pago de $" + request.getMonto() + " fue procesado exitosamente.");
            notificacion.setEmailDestino("cliente@gmail.com");

            webClient.post()
                    .uri("http://localhost:8088/api/notificaciones")
                    .bodyValue(notificacion)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            System.out.println("No se pudo enviar notificacion: " + e.getMessage());
        }

        return convertirAResponse(guardado);
    }

    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPago(Long id) {
        return pagoRepository.findById(id);
    }

    public List<Pago> obtenerPagosPorReserva(Long reservaId) {
        return pagoRepository.findByReservaId(reservaId);
    }

    public Pago actualizarEstado(Long id, EstadoPago estado) {
        Optional<Pago> pago = pagoRepository.findById(id);
        if (pago.isPresent()) {
            pago.get().setEstado(estado);
            return pagoRepository.save(pago.get());
        }
        throw new RuntimeException("Pago no encontrado");
    }

    private PagoResponse convertirAResponse(Pago pago) {
        PagoResponse response = new PagoResponse();
        response.setId(pago.getId());
        response.setReservaId(pago.getReservaId());
        response.setMonto(pago.getMonto());
        response.setEstado(pago.getEstado());
        response.setMetodoPago(pago.getMetodoPago());
        response.setFechaPago(pago.getFechaPago());
        return response;
    }
}