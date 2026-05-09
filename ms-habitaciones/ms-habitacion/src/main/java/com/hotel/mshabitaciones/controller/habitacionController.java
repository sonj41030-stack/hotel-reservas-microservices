package com.hotel.mshabitaciones.controller;


import com.hotel.mshabitaciones.dto.habitacionRequestDTO;
import com.hotel.mshabitaciones.dto.habitacionResponseDTO;
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
public class habitacionController {

    private  final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<habitacionResponseDTO>> obtenerTodos(){
        log.info("GET /api/habitaciones - Obteniendo todas las habitaciones");
        List<habitacionResponseDTO> habitaciones = habitacionService.obtenerTodos();
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<habitacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/habitaciones/{} - Buscando habitacion", id);
        habitacionResponseDTO habitacion = habitacionService.obtenerPorId(id);
        return ResponseEntity.ok(habitacion);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<habitacionResponseDTO>> obtenerPorHotel(@PathVariable Long hotelId) {
        log.info("GET /api/habitaciones/hotel/{} - Buscando habitaciones", hotelId);
        List<habitacionResponseDTO> habitaciones = habitacionService.obtenerPorHotel(hotelId);
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<habitacionResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/habitaciones/disponibles - Buscando habitaciones disponibles");
        List<habitacionResponseDTO> habitaciones = habitacionService.obtenerDisponibles();
        return ResponseEntity.ok(habitaciones);
    }

    @GetMapping("/mascotas")
    public ResponseEntity<List<habitacionResponseDTO>> obtenerPermiteMascotas() {
        log.info("GET /api/habitaciones/mascotas - Buscando habitaciones que permiten mascotas");
        List<habitacionResponseDTO> habitaciones = habitacionService.obtenerPermiteMascotas();
        return ResponseEntity.ok(habitaciones);
    }

    @PostMapping
    public ResponseEntity<habitacionResponseDTO> crear(@Valid @RequestBody habitacionRequestDTO dto) {
        log.info("POST /api/habitaciones - Creando habitacion tipo: {}", dto.getTipo());
        habitacionResponseDTO habitacion = habitacionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(habitacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<habitacionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody habitacionRequestDTO dto) {
        log.info("PUT /api/habitaciones/{} - Actualizando habitacion", id);
        habitacionResponseDTO habitacion = habitacionService.actualizar(id, dto);
        return ResponseEntity.ok(habitacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/habitaciones/{} - Eliminando habitacion", id);
        habitacionService.eliminar(id);
        return ResponseEntity.ok("Habitacion eliminada correctamente");
    }


}
