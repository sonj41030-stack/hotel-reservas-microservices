package com.hotel.msservicios.controller;

import com.hotel.msservicios.dto.servicioRequestDTO;
import com.hotel.msservicios.dto.servicioResponseDTO;
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
public class servicioController {

    private final ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<servicioResponseDTO>> obtenerTodos() {
        log.info("GET /api/servicios - Obteniendo todos los servicios");
        List<servicioResponseDTO> servicios = servicioService.obtenerTodos();
        return ResponseEntity.ok(servicios);
    }
    @GetMapping("/{id}")
    public ResponseEntity<servicioResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/servicios/{} - Buscando servicio", id);
        servicioResponseDTO servicio = servicioService.obtenerPorId(id);
        return ResponseEntity.ok(servicio);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<servicioResponseDTO>> obtenerPorTipo(@PathVariable String tipo) {
        log.info("GET /api/servicios/tipo/{} - Buscando servicios", tipo);
        List<servicioResponseDTO> servicios = servicioService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(servicios);
    }
    @GetMapping("/disponibles")
    public ResponseEntity<List<servicioResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/servicios/disponibles - Buscando servicios disponibles");
        List<servicioResponseDTO> servicios = servicioService.obtenerDisponibles();
        return ResponseEntity.ok(servicios);
    }

    @PostMapping
    public ResponseEntity<servicioResponseDTO> crear(@Valid @RequestBody servicioRequestDTO dto) {
        log.info("POST /api/servicios - Creando servicio: {}", dto.getNombre());
        servicioResponseDTO servicio = servicioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio);
    }
    @PutMapping("/{id}")
    public ResponseEntity<servicioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody servicioRequestDTO dto) {
        log.info("PUT /api/servicios/{} - Actualizando servicio", id);
        servicioResponseDTO servicio = servicioService.actualizar(id, dto);
        return ResponseEntity.ok(servicio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/servicios/{} - Eliminando servicio", id);
        servicioService.eliminar(id);
        return ResponseEntity.ok("Servicio eliminado correctamente");
    }

}
