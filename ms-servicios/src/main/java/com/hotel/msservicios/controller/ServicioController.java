package com.hotel.msservicios.controller;

import com.hotel.msservicios.dto.ServicioRequestDTO;
import com.hotel.msservicios.dto.ServicioResponseDTO;
import com.hotel.msservicios.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> obtenerTodos() {
        log.info("GET /api/servicios - Obteniendo todos los servicios");
        List<ServicioResponseDTO> servicios = servicioService.obtenerTodos();
        return ResponseEntity.ok(servicios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/servicios/{} - Buscando servicio", id);
        ServicioResponseDTO servicio = servicioService.obtenerPorId(id);
        return ResponseEntity.ok(servicio);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ServicioResponseDTO>> obtenerPorTipo(@PathVariable String tipo) {
        log.info("GET /api/servicios/tipo/{} - Buscando servicios", tipo);
        List<ServicioResponseDTO> servicios = servicioService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(servicios);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ServicioResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/servicios/disponibles - Buscando servicios disponibles");
        List<ServicioResponseDTO> servicios = servicioService.obtenerDisponibles();
        return ResponseEntity.ok(servicios);
    }

    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crear(
            @Valid @RequestBody ServicioRequestDTO dto) {

        log.info("POST /api/servicios - Creando servicio: {}", dto.getNombre());

        ServicioResponseDTO servicio = servicioService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(servicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO dto) {

        log.info("PUT /api/servicios/{} - Actualizando servicio", id);

        ServicioResponseDTO servicio = servicioService.actualizar(id, dto);

        return ResponseEntity.ok(servicio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {

        log.info("DELETE /api/servicios/{} - Eliminando servicio", id);

        servicioService.eliminar(id);

        return ResponseEntity.ok("Servicio eliminado correctamente");
    }
}
