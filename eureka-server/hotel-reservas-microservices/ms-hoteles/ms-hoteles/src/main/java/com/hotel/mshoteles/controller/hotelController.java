package com.hotel.mshoteles.controller;


import com.hotel.mshoteles.dto.hotelRequestDTO;
import com.hotel.mshoteles.dto.hotelResponseDTO;
import com.hotel.mshoteles.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/hoteles")
@RequiredArgsConstructor
public class hotelController {

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<hotelResponseDto>> obtenerTodos(){
        log.info("GET /api/hoteles - Obteniendo todos los hoteles");
        List<hotelResponseDTO> hoteles = hotelService.obtenerTodos();
        return ResponseEntity.ok(hoteles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<hotelResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/hoteles/{} - Buscando hotel", id);
        hotelResponseDTO hotel = hotelService.obtenerPorId(id);
        return ResponseEntity.ok(hotel);
    }
    @GetMapping("/cuidad/{cuidad}")
    public ResponseEntity<List<hotelResponseDTO>> obtenerPorCuidad(@PathVariable String cuidad) {
        log.info("GET /api/hoteles/cuidad/{} - Buscando hoteles", cuidad);
        List<hotelResponseDTO> hoteles = hotelService.obtenerPorCuidad(cuidad);
        return ResponseEntity.ok(hoteles);
    }
    @PostMapping
    public ResponseEntity<hotelResponseDTO> crear(@Valid @RequestBody hotelRequestDTO dto) {
        log.info("POST /api/hoteles - Creando hotel: {}", dto.getNombre());
        hotelResponseDTO hotel = hotelService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotel);
    }
    @PostMapping
    public ResponseEntity<hotelResponseDTO> crear(@Valid @RequestBody hotelRequestDTO dto) {
        log.info("POST /api/hoteles - Creando hotel: {}", dto.getNombre());
        hotelResponseDTO hotel = hotelService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotel);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/hoteles/{} - Eliminando hotel", id);
        hotelService.eliminar(id);
        return ResponseEntity.ok("Hotel eliminado correctamente");
    }
}
