package com.hotel.msreserva.controller;

import com.hotel.msreserva.dto.ReservaRequest;
import com.hotel.msreserva.model.EstadoReserva;
import com.hotel.msreserva.model.Reserva;
import com.hotel.msreserva.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.ok(reservaService.crearReserva(request));
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> listarReservas() {
        return ResponseEntity.ok(reservaService.listarReservas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReserva(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.obtenerReserva(id);
        if (reserva.isPresent()) {
            return ResponseEntity.ok(reserva.get());
        }
        return ResponseEntity.status(404).body("Reserva no encontrada");
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorCliente(clienteId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.ok("Reserva eliminada correctamente");
    }
}