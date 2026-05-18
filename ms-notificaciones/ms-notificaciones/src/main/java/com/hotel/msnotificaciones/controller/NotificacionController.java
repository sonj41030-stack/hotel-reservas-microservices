package com.hotel.msnotificaciones.controller;

import com.hotel.msnotificaciones.dto.NotificacionRequestDTO;
import com.hotel.msnotificaciones.dto.NotificacionResponseDTO;
import com.hotel.msnotificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // GET /api/notificaciones
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listar() {
        log.info("[NotificacionController] GET /api/notificaciones");
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    // GET /api/notificaciones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtener(@PathVariable Long id) {
        log.info("[NotificacionController] GET /api/notificaciones/{}", id);
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    // POST /api/notificaciones
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(
            @Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("[NotificacionController] POST /api/notificaciones");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificacionService.crear(dto));
    }

    // PUT /api/notificaciones/{id}/enviar
    @PutMapping("/{id}/enviar")
    public ResponseEntity<NotificacionResponseDTO> marcarEnviada(@PathVariable Long id) {
        log.info("[NotificacionController] PUT /api/notificaciones/{}/enviar", id);
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }

    // PUT /api/notificaciones/{id}/fallar
    @PutMapping("/{id}/fallar")
    public ResponseEntity<NotificacionResponseDTO> marcarFallida(@PathVariable Long id) {
        log.info("[NotificacionController] PUT /api/notificaciones/{}/fallar", id);
        return ResponseEntity.ok(notificacionService.marcarComoFallida(id));
    }

    // PUT /api/notificaciones/{id}/reenviar
    @PutMapping("/{id}/reenviar")
    public ResponseEntity<NotificacionResponseDTO> reenviar(@PathVariable Long id) {
        log.info("[NotificacionController] PUT /api/notificaciones/{}/reenviar", id);
        return ResponseEntity.ok(notificacionService.reenviar(id));
    }

    // DELETE /api/notificaciones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[NotificacionController] DELETE /api/notificaciones/{}", id);
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/notificaciones/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotificacionResponseDTO>> porCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(notificacionService.obtenerPorCliente(clienteId));
    }

    // GET /api/notificaciones/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> porEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(notificacionService.obtenerPorEstado(estado));
    }

    // GET /api/notificaciones/reserva/{reservaId}
    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<NotificacionResponseDTO>> porReserva(
            @PathVariable Long reservaId) {
        return ResponseEntity.ok(notificacionService.obtenerPorReserva(reservaId));
    }
}