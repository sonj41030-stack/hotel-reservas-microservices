package com.hotel.msnotificaciones.controller;

import com.hotel.msnotificaciones.dto.NotificacionRequestDTO;
import com.hotel.msnotificaciones.dto.NotificacionResponseDTO;
import com.hotel.msnotificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Operaciones para gestionar notificaciones enviadas a clientes del hotel")
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las notificaciones",
            description = "Retorna la lista completa de notificaciones registradas en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<NotificacionResponseDTO>> listar() {
        log.info("[NotificacionController] GET /api/notificaciones");
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener notificación por ID",
            description = "Busca y retorna una notificación específica según su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    public ResponseEntity<NotificacionResponseDTO> obtener(
            @Parameter(description = "ID de la notificación", required = true, example = "1")
            @PathVariable Long id) {
        log.info("[NotificacionController] GET /api/notificaciones/{}", id);
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Crear nueva notificación",
            description = "Crea una nueva notificación en estado PENDIENTE. Valida que el cliente exista en ms-clientes. " +
                    "Tipos válidos: CONFIRMACION_RESERVA, RECORDATORIO, ALERTA_SISTEMA, CANCELACION"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Notificación creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o cliente no encontrado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente no existe en ms-clientes", content = @Content)
    })
    public ResponseEntity<NotificacionResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la notificación a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = NotificacionRequestDTO.class))
            )
            @Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("[NotificacionController] POST /api/notificaciones");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificacionService.crear(dto));
    }

    @PutMapping("/{id}/enviar")
    @Operation(
            summary = "Marcar notificación como enviada",
            description = "Cambia el estado de una notificación a ENVIADA. Solo funciona si la notificación está en estado PENDIENTE."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación marcada como enviada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "La notificación no está en estado PENDIENTE", content = @Content)
    })
    public ResponseEntity<NotificacionResponseDTO> marcarEnviada(
            @Parameter(description = "ID de la notificación a enviar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("[NotificacionController] PUT /api/notificaciones/{}/enviar", id);
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }

    @PutMapping("/{id}/fallar")
    @Operation(
            summary = "Marcar notificación como fallida",
            description = "Cambia el estado de una notificación a FALLIDA. No se puede aplicar si ya fue ENVIADA."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación marcada como fallida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "No se puede marcar como fallida una notificación ya enviada", content = @Content)
    })
    public ResponseEntity<NotificacionResponseDTO> marcarFallida(
            @Parameter(description = "ID de la notificación a marcar como fallida", required = true, example = "1")
            @PathVariable Long id) {
        log.info("[NotificacionController] PUT /api/notificaciones/{}/fallar", id);
        return ResponseEntity.ok(notificacionService.marcarComoFallida(id));
    }

    @PutMapping("/{id}/reenviar")
    @Operation(
            summary = "Reenviar notificación fallida",
            description = "Cambia el estado de una notificación FALLIDA de vuelta a PENDIENTE para intentar reenviarla. " +
                    "Solo funciona si la notificación está en estado FALLIDA."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación puesta en cola para reenvío exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "La notificación no está en estado FALLIDA", content = @Content)
    })
    public ResponseEntity<NotificacionResponseDTO> reenviar(
            @Parameter(description = "ID de la notificación a reenviar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("[NotificacionController] PUT /api/notificaciones/{}/reenviar", id);
        return ResponseEntity.ok(notificacionService.reenviar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar notificación",
            description = "Elimina permanentemente una notificación del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la notificación a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("[NotificacionController] DELETE /api/notificaciones/{}", id);
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(
            summary = "Obtener notificaciones por cliente",
            description = "Retorna todas las notificaciones asociadas a un cliente específico"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de notificaciones del cliente obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = NotificacionResponseDTO.class))
    )
    public ResponseEntity<List<NotificacionResponseDTO>> porCliente(
            @Parameter(description = "ID del cliente", required = true, example = "5")
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(notificacionService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(
            summary = "Obtener notificaciones por estado",
            description = "Retorna todas las notificaciones con el estado indicado. Estados válidos: PENDIENTE, ENVIADA, FALLIDA"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de notificaciones obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = NotificacionResponseDTO.class))
    )
    public ResponseEntity<List<NotificacionResponseDTO>> porEstado(
            @Parameter(description = "Estado de la notificación", required = true, example = "PENDIENTE")
            @PathVariable String estado) {
        return ResponseEntity.ok(notificacionService.obtenerPorEstado(estado));
    }

    @GetMapping("/reserva/{reservaId}")
    @Operation(
            summary = "Obtener notificaciones por reserva",
            description = "Retorna todas las notificaciones asociadas a una reserva específica"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de notificaciones de la reserva obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = NotificacionResponseDTO.class))
    )
    public ResponseEntity<List<NotificacionResponseDTO>> porReserva(
            @Parameter(description = "ID de la reserva", required = true, example = "10")
            @PathVariable Long reservaId) {
        return ResponseEntity.ok(notificacionService.obtenerPorReserva(reservaId));
    }
}