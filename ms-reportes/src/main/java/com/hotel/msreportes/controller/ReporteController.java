package com.hotel.msreportes.controller;

import com.hotel.msreportes.dto.ReporteDTO;
import com.hotel.msreportes.model.Reporte;
import com.hotel.msreportes.model.TiposReporte;
import com.hotel.msreportes.service.ReporteService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Operaciones para generar y consultar reportes de ocupación, ingresos y reservas del hotel")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los reportes",
            description = "Retorna la lista completa de reportes registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reportes obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reporte.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<Reporte>> listar() {
        log.info("GET /api/reportes");
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener reporte por ID",
            description = "Busca y retorna un reporte específico según su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reporte encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reporte.class))
            ),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado", content = @Content)
    })
    public ResponseEntity<Reporte> obtener(
            @Parameter(description = "ID del reporte a buscar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/reportes/{}", id);
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Generar nuevo reporte",
            description = "Genera un nuevo reporte. Valida que la fecha inicio no sea posterior a la fecha fin, " +
                    "que el porcentaje de ocupación esté entre 0 y 100, y que el hotel exista en ms-hoteles. " +
                    "Tipos válidos: OCUPACION, INGRESOS, RESERVAS, GENERAL"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reporte generado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reporte.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o fechas incorrectas", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hotel no encontrado en ms-hoteles", content = @Content)
    })
    public ResponseEntity<Reporte> generar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del reporte a generar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReporteDTO.class))
            )
            @Valid @RequestBody ReporteDTO dto) {
        log.info("POST /api/reportes - tipo: {}", dto.getTipo());
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.generar(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar reporte",
            description = "Actualiza los datos de un reporte existente. Valida que la fecha inicio no sea posterior a la fecha fin."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reporte actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reporte.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o fechas incorrectas", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado", content = @Content)
    })
    public ResponseEntity<Reporte> actualizar(
            @Parameter(description = "ID del reporte a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del reporte",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReporteDTO.class))
            )
            @Valid @RequestBody ReporteDTO dto) {
        log.info("PUT /api/reportes/{}", id);
        return ResponseEntity.ok(reporteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar reporte",
            description = "Elimina permanentemente un reporte del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reporte eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del reporte a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/reportes/{}", id);
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(
            summary = "Obtener reportes por tipo",
            description = "Retorna todos los reportes del tipo indicado. Tipos válidos: OCUPACION, INGRESOS, RESERVAS, GENERAL"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de reportes obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Reporte.class))
    )
    public ResponseEntity<List<Reporte>> porTipo(
            @Parameter(description = "Tipo de reporte", required = true, example = "OCUPACION")
            @PathVariable TiposReporte tipo) {
        log.info("GET /api/reportes/tipo/{}", tipo);
        return ResponseEntity.ok(reporteService.obtenerPorTipo(tipo));
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(
            summary = "Obtener reportes por hotel",
            description = "Retorna todos los reportes asociados a un hotel específico"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de reportes del hotel obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Reporte.class))
    )
    public ResponseEntity<List<Reporte>> porHotel(
            @Parameter(description = "ID del hotel", required = true, example = "1")
            @PathVariable Long hotelId) {
        log.info("GET /api/reportes/hotel/{}", hotelId);
        return ResponseEntity.ok(reporteService.obtenerPorHotel(hotelId));
    }

    @GetMapping("/rango")
    @Operation(
            summary = "Obtener reportes por rango de fechas",
            description = "Retorna todos los reportes cuya fecha de inicio está dentro del rango indicado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reportes en el rango obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Reporte.class))
            ),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas", content = @Content)
    })
    public ResponseEntity<List<Reporte>> porRango(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/reportes/rango?inicio={}&fin={}", inicio, fin);
        return ResponseEntity.ok(reporteService.obtenerPorRango(inicio, fin));
    }

    @GetMapping("/ingresos")
    @Operation(
            summary = "Calcular ingresos totales de un hotel",
            description = "Calcula y retorna el total de ingresos de un hotel en un período específico, " +
                    "sumando todos los reportes de ese hotel dentro del rango de fechas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ingresos calculados exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = "{\"hotelId\": 1, \"periodo\": \"2025-01-01 al 2025-12-31\", \"totalIngresos\": 5000000.0}"))
            ),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> ingresosTotales(
            @Parameter(description = "ID del hotel", required = true, example = "1")
            @RequestParam Long hotelId,
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)", required = true, example = "2025-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/reportes/ingresos hotel={}", hotelId);
        Double total = reporteService.calcularIngresosTotales(hotelId, inicio, fin);
        return ResponseEntity.ok(Map.of(
                "hotelId", hotelId,
                "periodo", inicio + " al " + fin,
                "totalIngresos", total
        ));
    }
}