package com.hotel.mshousekeeping.controller;

import com.hotel.mshousekeeping.dto.TareaDTO;
import com.hotel.mshousekeeping.model.TareaHousekeeping;
import com.hotel.mshousekeeping.service.TareaService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/housekeeping")
@Tag(name = "Housekeeping", description = "Operaciones para gestionar tareas de limpieza y mantenimiento de habitaciones")
public class TareaController {

    private static final Logger log = LoggerFactory.getLogger(TareaController.class);
    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las tareas",
            description = "Retorna la lista completa de tareas de housekeeping registradas en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tareas obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TareaHousekeeping.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<TareaHousekeeping>> listar() {
        log.info("GET /api/housekeeping");
        return ResponseEntity.ok(tareaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener tarea por ID",
            description = "Busca y retorna una tarea de housekeeping específica según su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarea encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TareaHousekeeping.class))
            ),
            @ApiResponse(responseCode = "400", description = "Tarea no encontrada", content = @Content)
    })
    public ResponseEntity<TareaHousekeeping> obtener(
            @Parameter(description = "ID de la tarea a buscar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/housekeeping/{}", id);
        return ResponseEntity.ok(tareaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Crear nueva tarea",
            description = "Crea una nueva tarea de housekeeping. Valida que la habitación exista en ms-habitaciones."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarea creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TareaHousekeeping.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o habitación no encontrada", content = @Content)
    })
    public ResponseEntity<TareaHousekeeping> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la tarea a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TareaDTO.class))
            )
            @Valid @RequestBody TareaDTO dto) {
        log.info("POST /api/housekeeping - habitacion: {}", dto.getHabitacionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaService.crear(dto));
    }

    @PutMapping("/{id}/estado")
    @Operation(
            summary = "Cambiar estado de una tarea",
            description = "Actualiza el estado de una tarea. Estados válidos: PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA. No se puede modificar una tarea ya COMPLETADA o CANCELADA."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TareaHousekeeping.class))
            ),
            @ApiResponse(responseCode = "400", description = "Estado inválido o tarea no modificable", content = @Content)
    })
    public ResponseEntity<TareaHousekeeping> cambiarEstado(
            @Parameter(description = "ID de la tarea", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado (PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA)", required = true, example = "COMPLETADA")
            @RequestParam String valor) {
        log.info("PUT /api/housekeeping/{}/estado - nuevo estado: {}", id, valor);
        return ResponseEntity.ok(tareaService.cambiarEstado(id, valor));
    }

    @PutMapping("/{id}/asignar")
    @Operation(
            summary = "Asignar empleado a una tarea",
            description = "Asigna un empleado a una tarea de housekeeping. Si la tarea está PENDIENTE, pasa automáticamente a EN_PROCESO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado asignado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TareaHousekeeping.class))
            ),
            @ApiResponse(responseCode = "400", description = "Tarea no encontrada o empleado inválido", content = @Content)
    })
    public ResponseEntity<TareaHousekeeping> asignarEmpleado(
            @Parameter(description = "ID de la tarea", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nombre del empleado a asignar", required = true, example = "Juan Pérez")
            @RequestParam String empleado) {
        log.info("PUT /api/housekeeping/{}/asignar - empleado: {}", id, empleado);
        return ResponseEntity.ok(tareaService.asignarEmpleado(id, empleado));
    }

    @GetMapping("/habitacion/{habitacionId}")
    @Operation(
            summary = "Obtener tareas por habitación",
            description = "Retorna todas las tareas asociadas a una habitación específica"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de tareas de la habitación obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TareaHousekeeping.class))
    )
    public ResponseEntity<List<TareaHousekeeping>> porHabitacion(
            @Parameter(description = "ID de la habitación", required = true, example = "5")
            @PathVariable Long habitacionId) {
        return ResponseEntity.ok(tareaService.obtenerPorHabitacion(habitacionId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(
            summary = "Obtener tareas por estado",
            description = "Retorna todas las tareas que tienen el estado indicado. Estados válidos: PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de tareas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TareaHousekeeping.class))
    )
    public ResponseEntity<List<TareaHousekeeping>> porEstado(
            @Parameter(description = "Estado de la tarea", required = true, example = "PENDIENTE")
            @PathVariable String estado) {
        return ResponseEntity.ok(tareaService.obtenerPorEstado(estado));
    }

    @GetMapping("/empleado/{empleado}")
    @Operation(
            summary = "Obtener tareas por empleado",
            description = "Retorna todas las tareas asignadas a un empleado específico"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de tareas del empleado obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TareaHousekeeping.class))
    )
    public ResponseEntity<List<TareaHousekeeping>> porEmpleado(
            @Parameter(description = "Nombre del empleado", required = true, example = "Juan Pérez")
            @PathVariable String empleado) {
        return ResponseEntity.ok(tareaService.obtenerPorEmpleado(empleado));
    }

    @GetMapping("/prioridad/{prioridad}")
    @Operation(
            summary = "Obtener tareas por prioridad",
            description = "Retorna todas las tareas con la prioridad indicada. Valores válidos: BAJA, MEDIA, ALTA"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de tareas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TareaHousekeeping.class))
    )
    public ResponseEntity<List<TareaHousekeeping>> porPrioridad(
            @Parameter(description = "Prioridad de la tarea", required = true, example = "ALTA")
            @PathVariable String prioridad) {
        return ResponseEntity.ok(tareaService.obtenerPorPrioridad(prioridad));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar tarea",
            description = "Elimina permanentemente una tarea de housekeeping del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarea eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Tarea no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la tarea a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/housekeeping/{}", id);
        tareaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}