package com.hotel.msservicios.controller;

import com.hotel.msservicios.dto.ServicioRequestDTO;
import com.hotel.msservicios.dto.ServicioResponseDTO;
import com.hotel.msservicios.service.ServicioService;
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
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Gestión de servicios adicionales de un hotel (spa, desayuno, etc.)")
public class ServicioController {

    private final ServicioService servicioService;

    @Operation(summary = "Listar todos los servicios activos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(schema = @Schema(implementation = ServicioResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> obtenerTodos() {
        log.info("GET /api/servicios - Obteniendo todos los servicios");
        List<ServicioResponseDTO> servicios = servicioService.obtenerTodos();
        return ResponseEntity.ok(servicios);
    }

    @Operation(summary = "Obtener un servicio por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un servicio con ese id", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerPorId(
            @Parameter(description = "Id del servicio", example = "1")
            @PathVariable Long id) {

        log.info("GET /api/servicios/{} - Buscando servicio", id);
        ServicioResponseDTO servicio = servicioService.obtenerPorId(id);
        return ResponseEntity.ok(servicio);
    }

    @Operation(summary = "Listar servicios de un hotel específico")
    @ApiResponse(responseCode = "200", description = "Listado filtrado por hotel")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ServicioResponseDTO>> obtenerPorHotel(
            @Parameter(description = "Id del hotel", example = "1")
            @PathVariable Long hotelId) {

        log.info("GET /api/servicios/hotel/{} - Buscando servicios del hotel", hotelId);
        List<ServicioResponseDTO> servicios = servicioService.obtenerPorHotel(hotelId);
        return ResponseEntity.ok(servicios);
    }

    @Operation(summary = "Listar servicios por tipo", description = "Ej: Alimentacion, Spa, Transporte")
    @ApiResponse(responseCode = "200", description = "Listado filtrado por tipo")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ServicioResponseDTO>> obtenerPorTipo(
            @Parameter(description = "Tipo de servicio", example = "Alimentacion")
            @PathVariable String tipo) {

        log.info("GET /api/servicios/tipo/{} - Buscando servicios", tipo);
        List<ServicioResponseDTO> servicios = servicioService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(servicios);
    }

    @Operation(summary = "Listar servicios disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de servicios con disponible=true")
    @GetMapping("/disponibles")
    public ResponseEntity<List<ServicioResponseDTO>> obtenerDisponibles() {
        log.info("GET /api/servicios/disponibles - Buscando servicios disponibles");
        List<ServicioResponseDTO> servicios = servicioService.obtenerDisponibles();
        return ResponseEntity.ok(servicios);
    }

    @Operation(
            summary = "Crear un nuevo servicio",
            description = "Registra un servicio asociado a un hotel. El hotelId se valida " +
                    "contra ms-hoteles antes de guardar (vía Feign Client)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Servicio creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, nombre duplicado o hotelId inexistente", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crear(
            @Valid @RequestBody ServicioRequestDTO dto) {

        log.info("POST /api/servicios - Creando servicio: {}", dto.getNombre());

        ServicioResponseDTO servicio = servicioService.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(servicio);
    }

    @Operation(summary = "Actualizar un servicio existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un servicio con ese id", content = @Content),
            @ApiResponse(responseCode = "400", description = "hotelId inexistente", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizar(
            @Parameter(description = "Id del servicio a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO dto) {

        log.info("PUT /api/servicios/{} - Actualizando servicio", id);

        ServicioResponseDTO servicio = servicioService.actualizar(id, dto);

        return ResponseEntity.ok(servicio);
    }

    @Operation(summary = "Eliminar (desactivar) un servicio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un servicio con ese id", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "Id del servicio a eliminar", example = "1")
            @PathVariable Long id) {

        log.info("DELETE /api/servicios/{} - Eliminando servicio", id);

        servicioService.eliminar(id);

        return ResponseEntity.ok("Servicio eliminado correctamente");
    }
}
