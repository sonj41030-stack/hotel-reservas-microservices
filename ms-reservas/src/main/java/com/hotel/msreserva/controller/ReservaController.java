package com.hotel.msreserva.controller;

import com.hotel.msreserva.dto.ReservaRequest;
import com.hotel.msreserva.model.EstadoReserva;
import com.hotel.msreserva.model.Reserva;
import com.hotel.msreserva.service.ReservaService;
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
@RequestMapping("/reservas")
@RequiredArgsConstructor
@Tag(name = "ms-Reservas", description = "Microservicio para gestionar reservas del hotel")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Crear reserva", description = "Registra una nueva reserva en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Reserva> crearReserva(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.ok(reservaService.crearReserva(request));
    }

    @GetMapping
    @Operation(summary = "Listar reservas", description = "Retorna la lista de todas las reservas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    public ResponseEntity<List<Reserva>> listarReservas() {
        return ResponseEntity.ok(reservaService.listarReservas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID", description = "Retorna una reserva específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<?> obtenerReserva(
            @Parameter(description = "ID de la reserva", required = true)
            @PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.obtenerReserva(id);
        if (reserva.isPresent()) {
            return ResponseEntity.ok(reserva.get());
        }
        return ResponseEntity.status(404).body("Reserva no encontrada");
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener reservas por cliente", description = "Retorna todas las reservas de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<List<Reserva>> obtenerReservasPorCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorCliente(clienteId));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de reserva", description = "Cambia el estado de una reserva existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<?> actualizarEstado(
            @Parameter(description = "ID de la reserva", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado de la reserva", required = true)
            @RequestParam EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reserva", description = "Elimina una reserva del sistema por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<?> eliminarReserva(
            @Parameter(description = "ID de la reserva", required = true)
            @PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.ok("Reserva eliminada correctamente");
    }
}