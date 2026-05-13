package com.hotel.mspagos.controller;

import com.hotel.mspagos.dto.PagoRequest;
import com.hotel.mspagos.dto.PagoResponse;
import com.hotel.mspagos.model.EstadoPago;
import com.hotel.mspagos.model.Pago;
import com.hotel.mspagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponse> procesarPago(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.ok(pagoService.procesarPago(request));
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listarPagos() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPago(@PathVariable Long id) {
        Optional<Pago> pago = pagoService.obtenerPago(id);
        if (pago.isPresent()) {
            return ResponseEntity.ok(pago.get());
        }
        return ResponseEntity.status(404).body("Pago no encontrado");
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<Pago>> obtenerPagosPorReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorReserva(reservaId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam EstadoPago estado) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, estado));
    }
}