package com.hotel.mspagos.controller;

import com.hotel.mspagos.dto.PagoRequest;
import com.hotel.mspagos.dto.PagoResponse;
import com.hotel.mspagos.model.EstadoPago;
import com.hotel.mspagos.model.Pago;
import com.hotel.mspagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Tag(name = "ms-Pagos", description = "Microservicio para gestionar pagos del hotel")
public class PagoController {

    private final PagoService pagoService = null;

    @PostMapping
    @Operation(summary = "Procesar pago", description = "Registra y procesa un nuevo pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago procesado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<PagoResponse> procesarPago(
            @Valid @PathVariable PagoRequest request) {
        return ResponseEntity.ok(pagoService.procesarPago(request));
    }

    @GetMapping
    @Operation(summary = "Listar pagos", description = "Retorna la lista de todos los pagos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    public ResponseEntity<List<Pago>> listarPagos() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID", description = "Retorna un pago específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<?> obtenerPago(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable Long id) {
        Optional<Pago> pago = pagoService.obtenerPago(id);
        if (pago.isPresent()) {
            return ResponseEntity.ok(pago.get());
        }
        return ResponseEntity.status(404).body("Pago no encontrado");
    }

    @GetMapping("/reserva/{reservaId}")
    @Operation(summary = "Obtener pagos por reserva", description = "Retorna todos los pagos asociados a una reserva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<List<Pago>> obtenerPagosPorReserva(
            @Parameter(description = "ID de la reserva", required = true)
            @PathVariable Long reservaId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorReserva(reservaId));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de pago", description = "Cambia el estado de un pago existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<?> actualizarEstado(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado del pago", required = true)
            @RequestParam EstadoPago estado) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, estado));
    }
}