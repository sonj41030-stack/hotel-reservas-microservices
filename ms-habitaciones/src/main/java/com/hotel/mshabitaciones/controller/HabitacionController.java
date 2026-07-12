package com.hotel.mshabitaciones.controller;

import com.hotel.mshabitaciones.dto.HabitacionRequestDTO;
import com.hotel.mshabitaciones.dto.HabitacionResponseDTO;
import com.hotel.mshabitaciones.service.HabitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Habitaciones", description = "Gestión de habitaciones asociadas a un hotel")
public class HabitacionController {

    private final HabitacionService habitacionService;

    @Operation(summary = "Listar todas las habitaciones activas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(schema = @Schema(implementation = HabitacionResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerTodos() {
        log.info("GET /api/habitaciones - Obteniendo todas las habitaciones");
        return ResponseEntity.ok(habitacionService.obtenerTodos());
    }

    @Operation(summary = "Obtener una habitación por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habitación encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una habitación con ese id", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponseDTO> obtenerPorId(
            @Parameter(description = "Id de la habitación", example = "1")
            @PathVariable Long id) {

        log.info("GET /api/habitaciones/{} - Buscando habitación", id);
        return ResponseEntity.ok(habitacionService.obtenerPorId(id));
    }

    @Operation(summary = "Listar habitaciones de un hotel específico")
    @ApiResponse(responseCode = "200", description = "Listado filtrado por hotel")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerPorHotel(
            @Parameter(description = "Id del hotel", example = "1")
            @PathVariable Long hotelId) {

        log.info("GET /api/habitaciones/hotel/{} - Buscando habitaciones", hotelId);
        return ResponseEntity.ok(habitacionService.obtenerPorHotel(hotelId));
    }

    @Operation(summary = "Listar habitaciones disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de habitaciones con disponible=true")
    @GetMapping("/disponibles")
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/habitaciones/disponibles - Buscando habitaciones disponibles");
        return ResponseEntity.ok(habitacionService.obtenerDisponibles());
    }

    @Operation(summary = "Listar habitaciones que permiten mascotas")
    @ApiResponse(responseCode = "200", description = "Listado de habitaciones con permiteMascotas=true")
    @GetMapping("/mascotas")
    public ResponseEntity<List<HabitacionResponseDTO>> obtenerPermiteMascotas() {
        log.info("GET /api/habitaciones/mascotas - Buscando habitaciones que permiten mascotas");
        return ResponseEntity.ok(habitacionService.obtenerPermiteMascotas());
    }

    @Operation(
            summary = "Crear una nueva habitación",
            description = "Registra una habitación asociada a un hotel. El hotelId se valida " +
                    "contra ms-hoteles antes de guardar (vía WebClient)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Habitación creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o hotelId inexistente", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HabitacionResponseDTO> crear(
            @Valid @RequestBody HabitacionRequestDTO dto) {

        log.info("POST /api/habitaciones - Creando habitación tipo: {}", dto.getTipo());

        HabitacionResponseDTO habitacion = habitacionService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(habitacion);
    }

    @Operation(summary = "Actualizar una habitación existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habitación actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe una habitación con ese id", content = @Content),
            @ApiResponse(responseCode = "400", description = "hotelId inexistente", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<HabitacionResponseDTO> actualizar(
            @Parameter(description = "Id de la habitación a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody HabitacionRequestDTO dto) {

        log.info("PUT /api/habitaciones/{} - Actualizando habitación", id);

        return ResponseEntity.ok(
                habitacionService.actualizar(id, dto)
        );
    }

    @Operation(summary = "Eliminar (desactivar) una habitación")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habitación eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe una habitación con ese id", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "Id de la habitación a eliminar", example = "1")
            @PathVariable Long id) {

        log.info("DELETE /api/habitaciones/{} - Eliminando habitación", id);

        habitacionService.eliminar(id);

        return ResponseEntity.ok("Habitación eliminada correctamente");
    }
}
