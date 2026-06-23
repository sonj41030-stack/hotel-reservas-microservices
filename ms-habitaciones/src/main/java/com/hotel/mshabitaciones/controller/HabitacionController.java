package com.hotel.mshabitaciones.controller;

import com.hotel.mshabitaciones.dto.HabitacionRequestDTO;
import com.hotel.mshabitaciones.dto.HabitacionResponseDTO;
import com.hotel.mshabitaciones.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerTodos() {
        log.info("GET /api/habitaciones - Obteniendo todas las habitaciones");
        return ResponseEntity.ok(habitacionService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/habitaciones/{} - Buscando habitación", id);
        return ResponseEntity.ok(habitacionService.obtenerPorId(id));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerPorHotel(@PathVariable Long hotelId) {
        log.info("GET /api/habitaciones/hotel/{} - Buscando habitaciones", hotelId);
        return ResponseEntity.ok(habitacionService.obtenerPorHotel(hotelId));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/habitaciones/disponibles - Buscando habitaciones disponibles");
        return ResponseEntity.ok(habitacionService.obtenerDisponibles());
    }

    @GetMapping("/mascotas")
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerPermiteMascotas() {
        log.info("GET /api/habitaciones/mascotas - Buscando habitaciones que permiten mascotas");
        return ResponseEntity.ok(habitacionService.obtenerPermiteMascotas());
    }

    @PostMapping
    public ResponseEntity<HabitacionResponseDTO> crear(
            @Valid @RequestBody HabitacionRequestDTO dto) {

        log.info("POST /api/habitaciones - Creando habitación tipo: {}", dto.getTipo());

        HabitacionResponseDTO habitacion = habitacionService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(habitacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody HabitacionRequestDTO dto) {

        log.info("PUT /api/habitaciones/{} - Actualizando habitación", id);

        return ResponseEntity.ok(
                habitacionService.actualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {

        log.info("DELETE /api/habitaciones/{} - Eliminando habitación", id);

        habitacionService.eliminar(id);

        return ResponseEntity.ok("Habitación eliminada correctamente");
    }
}