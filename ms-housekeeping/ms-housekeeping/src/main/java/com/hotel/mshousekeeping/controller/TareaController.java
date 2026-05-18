package com.hotel.mshousekeeping.controller;

import com.hotel.mshousekeeping.dto.TareaDTO;
import com.hotel.mshousekeeping.model.TareaHousekeeping;
import com.hotel.mshousekeeping.service.TareaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/housekeeping")
public class TareaController {

    private static final Logger log = LoggerFactory.getLogger(TareaController.class);
    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    // GET /api/housekeeping
    @GetMapping
    public ResponseEntity<List<TareaHousekeeping>> listar() {
        log.info("GET /api/housekeeping");
        return ResponseEntity.ok(tareaService.listarTodas());
    }

    // GET /api/housekeeping/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TareaHousekeeping> obtener(@PathVariable Long id) {
        log.info("GET /api/housekeeping/{}", id);
        return ResponseEntity.ok(tareaService.obtenerPorId(id));
    }

    // POST /api/housekeeping
    @PostMapping
    public ResponseEntity<TareaHousekeeping> crear(@Valid @RequestBody TareaDTO dto) {
        log.info("POST /api/housekeeping - habitacion: {}", dto.getHabitacionId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tareaService.crear(dto));
    }

    // PUT /api/housekeeping/{id}/estado?valor=COMPLETADA
    @PutMapping("/{id}/estado")
    public ResponseEntity<TareaHousekeeping> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String valor) {
        log.info("PUT /api/housekeeping/{}/estado - nuevo estado: {}", id, valor);
        return ResponseEntity.ok(tareaService.cambiarEstado(id, valor));
    }

    // PUT /api/housekeeping/{id}/asignar?empleado=Juan
    @PutMapping("/{id}/asignar")
    public ResponseEntity<TareaHousekeeping> asignarEmpleado(
            @PathVariable Long id,
            @RequestParam String empleado) {
        log.info("PUT /api/housekeeping/{}/asignar - empleado: {}", id, empleado);
        return ResponseEntity.ok(tareaService.asignarEmpleado(id, empleado));
    }

    // GET /api/housekeeping/habitacion/{habitacionId}
    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<TareaHousekeeping>> porHabitacion(
            @PathVariable Long habitacionId) {
        return ResponseEntity.ok(tareaService.obtenerPorHabitacion(habitacionId));
    }

    // GET /api/housekeeping/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaHousekeeping>> porEstado(
            @PathVariable String estado) {
        return ResponseEntity.ok(tareaService.obtenerPorEstado(estado));
    }

    // GET /api/housekeeping/empleado/{empleado}
    @GetMapping("/empleado/{empleado}")
    public ResponseEntity<List<TareaHousekeeping>> porEmpleado(
            @PathVariable String empleado) {
        return ResponseEntity.ok(tareaService.obtenerPorEmpleado(empleado));
    }

    // GET /api/housekeeping/prioridad/{prioridad}
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<TareaHousekeeping>> porPrioridad(
            @PathVariable String prioridad) {
        return ResponseEntity.ok(tareaService.obtenerPorPrioridad(prioridad));
    }

    // DELETE /api/housekeeping/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/housekeeping/{}", id);
        tareaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}