package com.hotel.msreportes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.msreportes.dto.ReporteDTO;
import com.hotel.msreportes.model.Reporte;
import com.hotel.msreportes.model.TiposReporte;
import com.hotel.msreportes.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests a nivel de CONTROLLER para ReporteController.
 *
 * @WebMvcTest: levanta SOLO la capa web (controller + MockMvc),
 * sin base de datos ni servicios reales.
 *
 * Aquí probamos:
 *   - URLs y métodos HTTP correctos
 *   - Status codes (200, 201, 204)
 *   - Estructura del JSON de respuesta
 */
@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    // MockMvc simula llamadas HTTP sin levantar un servidor real
    @Autowired
    private MockMvc mockMvc;

    // Mock del servicio: el controller llama a este, no al real
    @MockBean
    private ReporteService reporteService;

    // ObjectMapper con soporte para LocalDate (JavaTimeModule)
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 1: GET /api/reportes → 200 OK con lista
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testListar() throws Exception {
        // 1. El servicio devuelve un reporte de prueba
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.OCUPACION);
        when(reporteService.listarTodos()).thenReturn(List.of(reporte));

        // 2 y 3. Simular GET y verificar respuesta
        mockMvc.perform(get("/api/reportes"))
                .andExpect(status().isOk())                          // 200
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipo").value("OCUPACION"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 2: GET /api/reportes/{id} → 200 OK con el reporte
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorId() throws Exception {
        // 1. El servicio devuelve el reporte con id=1
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.INGRESOS);
        when(reporteService.obtenerPorId(1L)).thenReturn(reporte);

        // 2 y 3.
        mockMvc.perform(get("/api/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("INGRESOS"))
                .andExpect(jsonPath("$.hotelId").value(1));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 3: POST /api/reportes → 201 CREATED
    //
    // ¿Qué se espera en testGenerar?
    // Se envía un JSON con los datos del reporte al endpoint POST.
    // Se espera que el controller responda con HTTP 201 (Created) y que
    // el body contenga el reporte creado con su id y tipo correctos.
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testGenerar() throws Exception {
        // 1. Preparar el request body
        ReporteDTO dto = crearReporteDTO(
                TiposReporte.RESERVAS,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );

        Reporte guardado = crearReporteDePrueba(1L, TiposReporte.RESERVAS);
        when(reporteService.generar(any(ReporteDTO.class))).thenReturn(guardado);

        // 2. Simular POST con JSON en el body
        mockMvc.perform(post("/api/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                // 3. Verificar respuesta
                .andExpect(status().isCreated())                     // 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("RESERVAS"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 4: PUT /api/reportes/{id} → 200 OK con reporte actualizado
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testActualizar() throws Exception {
        // 1. DTO de actualización
        ReporteDTO dto = crearReporteDTO(
                TiposReporte.GENERAL,
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28)
        );

        Reporte actualizado = crearReporteDePrueba(1L, TiposReporte.GENERAL);
        when(reporteService.actualizar(eq(1L), any(ReporteDTO.class))).thenReturn(actualizado);

        // 2 y 3.
        mockMvc.perform(put("/api/reportes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("GENERAL"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 5: DELETE /api/reportes/{id} → 204 No Content
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testEliminar() throws Exception {
        // 1. El servicio elimina sin devolver nada (void)
        doNothing().when(reporteService).eliminar(1L);

        // 2 y 3.
        mockMvc.perform(delete("/api/reportes/1"))
                .andExpect(status().isNoContent());                  // 204

        verify(reporteService, times(1)).eliminar(1L);
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 6: GET /api/reportes/tipo/{tipo} → 200 OK con lista filtrada
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testPorTipo() throws Exception {
        // 1.
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.OCUPACION);
        when(reporteService.obtenerPorTipo(TiposReporte.OCUPACION)).thenReturn(List.of(reporte));

        // 2 y 3.
        mockMvc.perform(get("/api/reportes/tipo/OCUPACION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("OCUPACION"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 7: GET /api/reportes/hotel/{hotelId} → 200 OK
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testPorHotel() throws Exception {
        // 1.
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.INGRESOS);
        when(reporteService.obtenerPorHotel(1L)).thenReturn(List.of(reporte));

        // 2 y 3.
        mockMvc.perform(get("/api/reportes/hotel/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hotelId").value(1));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 8: GET /api/reportes/rango?inicio=...&fin=... → 200 OK
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testPorRango() throws Exception {
        // 1.
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.RESERVAS);
        when(reporteService.obtenerPorRango(any(), any())).thenReturn(List.of(reporte));

        // 2 y 3.
        mockMvc.perform(get("/api/reportes/rango")
                        .param("inicio", "2025-01-01")
                        .param("fin", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 9: GET /api/reportes/ingresos?hotelId=1&inicio=...&fin=...
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testIngresosTotales() throws Exception {
        // 1.
        when(reporteService.calcularIngresosTotales(eq(1L), any(), any()))
                .thenReturn(1500000.0);

        // 2 y 3.
        mockMvc.perform(get("/api/reportes/ingresos")
                        .param("hotelId", "1")
                        .param("inicio", "2025-01-01")
                        .param("fin", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotelId").value(1))
                .andExpect(jsonPath("$.totalIngresos").value(1500000.0));
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: crea un Reporte de prueba reutilizable para los mocks
    // ─────────────────────────────────────────────────────────────────
    private Reporte crearReporteDePrueba(Long id, TiposReporte tipo) {
        Reporte r = new Reporte();
        r.setId(id);
        r.setTipo(tipo);
        r.setFechaInicio(LocalDate.of(2025, 1, 1));
        r.setFechaFin(LocalDate.of(2025, 1, 31));
        r.setTotalReservas(50);
        r.setTotalIngresos(500000.0);
        r.setPorcentajeOcupacion(75.0);
        r.setHotelId(1L);
        r.setObservaciones("Reporte de prueba");
        return r;
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: crea un ReporteDTO de prueba reutilizable
    // ─────────────────────────────────────────────────────────────────
    private ReporteDTO crearReporteDTO(TiposReporte tipo, LocalDate inicio, LocalDate fin) {
        ReporteDTO dto = new ReporteDTO();
        dto.setTipo(tipo);
        dto.setFechaInicio(inicio);
        dto.setFechaFin(fin);
        dto.setTotalReservas(50);
        dto.setTotalIngresos(500000.0);
        dto.setPorcentajeOcupacion(75.0);
        dto.setHotelId(1L);
        dto.setObservaciones("DTO de prueba");
        return dto;
    }
}