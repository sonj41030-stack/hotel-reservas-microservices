package com.hotel.mshoteles.controller;


import com.hotel.mshoteles.dto.HotelRequestDTO;
import com.hotel.mshoteles.dto.HotelResponseDTO;
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
    public ResponseEntity<List<HotelResponseDTO>> obtenerTodos(){
        log.info("GET /api/hoteles - Obteniendo todos los hoteles");
        List<HotelResponseDTO> hoteles = hotelService.obtenerTodos();
        return ResponseEntity.ok(hoteles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/hoteles/{} - Buscando hotel", id);
        HotelResponseDTO hotel = hotelService.obtenerPorId(id);
        return ResponseEntity.ok(hotel);
    }
    @GetMapping("/cuidad/{cuidad}")
    public ResponseEntity<List<HotelResponseDTO>> obtenerPorCuidad(@PathVariable String cuidad) {
        log.info("GET /api/hoteles/cuidad/{} - Buscando hoteles", cuidad);
        List<HotelResponseDTO> hoteles = hotelService.obtenerPorCuidad(cuidad);
        return ResponseEntity.ok(hoteles);
    }
    @PostMapping
    public ResponseEntity<HotelResponseDTO> crear(@Valid @RequestBody HotelRequestDTO dto) {
        log.info("POST /api/hoteles - Creando hotel: {}", dto.getNombre());
        HotelResponseDTO hotel = hotelService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/hoteles/{} - Eliminando hotel", id);
        hotelService.eliminar(id);
        return ResponseEntity.ok("Hotel eliminado correctamente");
    }
}
