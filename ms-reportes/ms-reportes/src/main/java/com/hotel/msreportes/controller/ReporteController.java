package com.hotel.msreportes.controller;

import com.hotel.msreportes.dto.ReporteDTO;
import com.hotel.msreportes.model.Reporte;
import com.hotel.msreportes.model.TiposReporte;
import com.hotel.msreportes.service.ReporteService;
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
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // GET /api/reportes
    @GetMapping
    public ResponseEntity<List<Reporte>> listar() {
        log.info("GET /api/reportes");
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    // GET /api/reportes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtener(@PathVariable Long id) {
        log.info("GET /api/reportes/{}", id);
        return ResponseEntity.ok(reporteService.obtenerPorId(id));
    }

    // POST /api/reportes
    @PostMapping
    public ResponseEntity<Reporte> generar(@Valid @RequestBody ReporteDTO dto) {
        log.info("POST /api/reportes - tipo: {}", dto.getTipo());
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.generar(dto));
    }

    // PUT /api/reportes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(@PathVariable Long id,
                                              @Valid @RequestBody ReporteDTO dto) {
        log.info("PUT /api/reportes/{}", id);
        return ResponseEntity.ok(reporteService.actualizar(id, dto));
    }

    // DELETE /api/reportes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/reportes/{}", id);
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/reportes/tipo/OCUPACION
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Reporte>> porTipo(@PathVariable TiposReporte tipo) {
        log.info("GET /api/reportes/tipo/{}", tipo);
        return ResponseEntity.ok(reporteService.obtenerPorTipo(tipo));
    }

    // GET /api/reportes/hotel/{hotelId}
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Reporte>> porHotel(@PathVariable Long hotelId) {
        log.info("GET /api/reportes/hotel/{}", hotelId);
        return ResponseEntity.ok(reporteService.obtenerPorHotel(hotelId));
    }

    // GET /api/reportes/rango?inicio=2025-01-01&fin=2025-12-31
    @GetMapping("/rango")
    public ResponseEntity<List<Reporte>> porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        log.info("GET /api/reportes/rango?inicio={}&fin={}", inicio, fin);
        return ResponseEntity.ok(reporteService.obtenerPorRango(inicio, fin));
    }

    // GET /api/reportes/ingresos?hotelId=1&inicio=2025-01-01&fin=2025-12-31
    @GetMapping("/ingresos")
    public ResponseEntity<Map<String, Object>> ingresosTotales(
            @RequestParam Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
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