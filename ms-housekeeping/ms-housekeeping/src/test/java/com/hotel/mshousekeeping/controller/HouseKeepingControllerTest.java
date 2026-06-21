package com.hotel.mshousekeeping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.mshousekeeping.dto.TareaDTO;
import com.hotel.mshousekeeping.model.EstadoTarea;
import com.hotel.mshousekeeping.model.TareaHousekeeping;
import com.hotel.mshousekeeping.service.TareaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests a nivel de CONTROLLER para TareaController.
 *
 * @WebMvcTest(TareaController.class) → levanta solo la capa web, sin BD ni servicios reales.
 * MockMvc → simula peticiones HTTP sin servidor real.
 * @MockBean TareaService → el servicio es simulado, no ejecuta lógica real.
 *
 * Patrón de cada test:
 *   1. Configurar mock del servicio (when...thenReturn)
 *   2. Ejecutar petición HTTP con MockMvc (perform)
 *   3. Verificar código HTTP y cuerpo JSON (andExpect)
 */
@WebMvcTest(TareaController.class)
class HouseKeepingControllerTest {

    // Simula peticiones HTTP al controller
    @Autowired
    private MockMvc mockMvc;

    // Mock del servicio: el controller delega en este objeto simulado
    @MockBean
    private TareaService tareaService;

    // Convierte objetos Java ↔ JSON
    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────────────────────────
    // TEST 1: GET /api/housekeeping → debe retornar lista con HTTP 200
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testListar() throws Exception {
        // 1. El servicio retorna una tarea de prueba
        when(tareaService.listarTodas())
                .thenReturn(List.of(crearTareaDePrueba(1L, EstadoTarea.PENDIENTE)));

        // 2 y 3. GET y verificar 200 + JSON correcto
        mockMvc.perform(get("/api/housekeeping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipoTarea").value("LIMPIEZA"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 2: GET /api/housekeeping/{id} → debe retornar la tarea con HTTP 200
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtener() throws Exception {
        // 1. El servicio retorna la tarea con id=1
        when(tareaService.obtenerPorId(1L))
                .thenReturn(crearTareaDePrueba(1L, EstadoTarea.PENDIENTE));

        // 2 y 3.
        mockMvc.perform(get("/api/housekeeping/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.empleadoAsignado").value("Maria González"))
                .andExpect(jsonPath("$.prioridad").value("ALTA"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 3: POST /api/housekeeping → debe crear tarea y retornar HTTP 201
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCrearTarea() throws Exception {
        // 1. DTO válido y mock del servicio
        TareaDTO dto = crearTareaDTO("LIMPIEZA", "ALTA");
        when(tareaService.crear(any(TareaDTO.class)))
                .thenReturn(crearTareaDePrueba(1L, EstadoTarea.PENDIENTE));

        // 2 y 3. POST con JSON y verificar 201 + datos correctos
        mockMvc.perform(post("/api/housekeeping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoTarea").value("LIMPIEZA"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 4: POST /api/housekeeping con DTO inválido → debe retornar HTTP 400
    //         (Falla intencional: habitacionId=null y tipoTarea=null
    //          violan @NotNull y @NotBlank del TareaDTO)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCrearTareaInvalida() throws Exception {
        // 1. DTO vacío → habitacionId=null viola @NotNull
        //                tipoTarea=null  viola @NotBlank
        TareaDTO dtoInvalido = new TareaDTO();

        // 2 y 3. POST sin datos obligatorios → debe rechazar con 400
        mockMvc.perform(post("/api/housekeeping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 5: PUT /api/housekeeping/{id}/estado → debe cambiar estado con HTTP 200
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCambiarEstado() throws Exception {
        // 1. El servicio retorna la tarea con estado actualizado
        when(tareaService.cambiarEstado(eq(1L), eq("EN_PROCESO")))
                .thenReturn(crearTareaDePrueba(1L, EstadoTarea.EN_PROCESO));

        // 2 y 3.
        mockMvc.perform(put("/api/housekeeping/1/estado")
                        .param("valor", "EN_PROCESO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 6: PUT /api/housekeeping/{id}/asignar → debe asignar empleado con HTTP 200
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testAsignarEmpleado() throws Exception {
        // 1. Tarea con empleado asignado y en EN_PROCESO
        TareaHousekeeping tareaConEmpleado = crearTareaDePrueba(1L, EstadoTarea.EN_PROCESO);
        tareaConEmpleado.setEmpleadoAsignado("Carlos Pérez");
        when(tareaService.asignarEmpleado(eq(1L), eq("Carlos Pérez")))
                .thenReturn(tareaConEmpleado);

        // 2 y 3.
        mockMvc.perform(put("/api/housekeeping/1/asignar")
                        .param("empleado", "Carlos Pérez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empleadoAsignado").value("Carlos Pérez"))
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 7: DELETE /api/housekeeping/{id} → debe eliminar y retornar HTTP 204
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testEliminar() throws Exception {
        // 2 y 3. DELETE y verificar 204 sin cuerpo
        mockMvc.perform(delete("/api/housekeeping/1"))
                .andExpect(status().isNoContent());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 8: GET /api/housekeeping/estado/{estado} → debe filtrar por estado
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testPorEstado() throws Exception {
        // 1. El servicio retorna tareas PENDIENTES
        when(tareaService.obtenerPorEstado("PENDIENTE"))
                .thenReturn(List.of(crearTareaDePrueba(1L, EstadoTarea.PENDIENTE)));

        // 2 y 3.
        mockMvc.perform(get("/api/housekeeping/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: crea una TareaHousekeeping de prueba reutilizable
    // ─────────────────────────────────────────────────────────────────
    private TareaHousekeeping crearTareaDePrueba(Long id, EstadoTarea estado) {
        TareaHousekeeping t = new TareaHousekeeping();
        t.setId(id);
        t.setHabitacionId(10L);
        t.setTipoTarea("LIMPIEZA");
        t.setEstado(estado);
        t.setEmpleadoAsignado("Maria González");
        t.setPrioridad("ALTA");
        t.setObservaciones("Tarea de prueba");
        t.setFechaAsignacion(LocalDateTime.now());
        return t;
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: crea un TareaDTO de prueba reutilizable
    // ─────────────────────────────────────────────────────────────────
    private TareaDTO crearTareaDTO(String tipoTarea, String prioridad) {
        TareaDTO dto = new TareaDTO();
        dto.setHabitacionId(10L);
        dto.setTipoTarea(tipoTarea);
        dto.setEmpleadoAsignado("Maria González");
        dto.setPrioridad(prioridad);
        dto.setObservaciones("DTO de prueba");
        return dto;
    }
}
