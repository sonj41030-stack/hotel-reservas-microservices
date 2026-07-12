package com.hotel.msnotificaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.msnotificaciones.dto.NotificacionRequestDTO;
import com.hotel.msnotificaciones.dto.NotificacionResponseDTO;
import com.hotel.msnotificaciones.model.EstadoNotificacion;
import com.hotel.msnotificaciones.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests a nivel de CONTROLLER para NotificacionController.
 *
 * Se usa @WebMvcTest: levanta SOLO la capa web (controller + MockMvc),
 * sin base de datos ni servicios reales.
 *
 * El servicio se mockea con @MockBean, por eso no importa
 * lo que haga por dentro, solo controlamos qué devuelve.
 *
 * Diferencia clave vs tests de servicio:
 *   - Aquí probamos endpoints HTTP (URLs, status codes, JSON response)
 *   - En los de servicio probamos lógica de negocio (estados, validaciones)
 */
@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    // MockMvc simula llamadas HTTP sin levantar un servidor real
    @Autowired
    private MockMvc mockMvc;

    // Mock del servicio: el controller llama a este, no al real
    @MockBean
    private NotificacionService notificacionService;

    // Para convertir objetos Java → JSON en las requests
    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────────────────────────
    // TEST 1: GET /api/notificaciones → 200 OK con lista
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testListar() throws Exception {
        // 1. Configurar mock: el servicio devuelve una notificación
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionService.listarTodas()).thenReturn(List.of(dto));

        // 2 y 3. Simular GET y verificar respuesta HTTP
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())                         // 200
                .andExpect(jsonPath("$[0].id").value(1))            // primer elemento tiene id=1
                .andExpect(jsonPath("$[0].tipo").value("CONFIRMACION_RESERVA"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 2: GET /api/notificaciones/{id} → 200 OK con el objeto
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorId() throws Exception {
        // 1. El servicio devuelve una notificación con id=1
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionService.obtenerPorId(1L)).thenReturn(dto);

        // 2 y 3. Simular GET /api/notificaciones/1
        mockMvc.perform(get("/api/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 3: POST /api/notificaciones → 201 CREATED
    //
    // ¿Qué se espera en testCrearNotificacion?
    // Se envía un JSON con los datos de la notificación al endpoint POST.
    // Se espera que el controller responda con HTTP 201 (Created) y que
    // el body del response contenga la notificación recién creada,
    // incluyendo su id y estado PENDIENTE asignado automáticamente.
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCrearNotificacion() throws Exception {
        // 1. Preparar el request body (lo que envía el cliente)
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setClienteId(10L);
        request.setReservaId(5L);
        request.setTipo("CONFIRMACION_RESERVA");
        request.setMensaje("Tu reserva fue confirmada");
        request.setEmailDestino("cliente@hotel.com");

        // El servicio devuelve el DTO con id asignado y estado PENDIENTE
        NotificacionResponseDTO response = crearResponseDTO(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionService.crear(any(NotificacionRequestDTO.class))).thenReturn(response);

        // 2. Simular POST con JSON en el body
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // 3. Verificar respuesta
                .andExpect(status().isCreated())                        // 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.tipo").value("CONFIRMACION_RESERVA"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 4: PUT /api/notificaciones/{id}/enviar → 200 OK con estado ENVIADA
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testMarcarComoEnviada() throws Exception {
        // 1. El servicio devuelve la notificación con estado ENVIADA
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.ENVIADA);
        when(notificacionService.marcarComoEnviada(1L)).thenReturn(dto);

        // 2 y 3.
        mockMvc.perform(put("/api/notificaciones/1/enviar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADA"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 5: PUT /api/notificaciones/{id}/fallar → 200 OK con estado FALLIDA
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testMarcarComoFallida() throws Exception {
        // 1.
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.FALLIDA);
        when(notificacionService.marcarComoFallida(1L)).thenReturn(dto);

        // 2 y 3.
        mockMvc.perform(put("/api/notificaciones/1/fallar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FALLIDA"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 6: PUT /api/notificaciones/{id}/reenviar → 200 OK, vuelve a PENDIENTE
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testReenviar() throws Exception {
        // 1.
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionService.reenviar(1L)).thenReturn(dto);

        // 2 y 3.
        mockMvc.perform(put("/api/notificaciones/1/reenviar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 7: DELETE /api/notificaciones/{id} → 204 No Content
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testEliminar() throws Exception {
        // 1. El servicio elimina sin devolver nada (void)
        doNothing().when(notificacionService).eliminar(1L);

        // 2 y 3.
        mockMvc.perform(delete("/api/notificaciones/1"))
                .andExpect(status().isNoContent());  // 204

        // Extra: verificar que el servicio fue llamado
        verify(notificacionService, times(1)).eliminar(1L);
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 8: GET /api/notificaciones/cliente/{clienteId} → 200 OK
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testPorCliente() throws Exception {
        // 1.
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.ENVIADA);
        when(notificacionService.obtenerPorCliente(10L)).thenReturn(List.of(dto));

        // 2 y 3.
        mockMvc.perform(get("/api/notificaciones/cliente/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(10));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 9: GET /api/notificaciones/estado/{estado} → 200 OK
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testPorEstado() throws Exception {
        // 1.
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionService.obtenerPorEstado("PENDIENTE")).thenReturn(List.of(dto));

        // 2 y 3.
        mockMvc.perform(get("/api/notificaciones/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 10: GET /api/notificaciones/reserva/{reservaId} → 200 OK
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testPorReserva() throws Exception {
        // 1.
        NotificacionResponseDTO dto = crearResponseDTO(1L, EstadoNotificacion.ENVIADA);
        when(notificacionService.obtenerPorReserva(5L)).thenReturn(List.of(dto));

        // 2 y 3.
        mockMvc.perform(get("/api/notificaciones/reserva/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservaId").value(5));
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: crea un DTO de respuesta reutilizable para los mocks
    // ─────────────────────────────────────────────────────────────────
    private NotificacionResponseDTO crearResponseDTO(Long id, EstadoNotificacion estado) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setId(id);
        dto.setClienteId(10L);
        dto.setReservaId(5L);
        dto.setTipo("CONFIRMACION_RESERVA");
        dto.setMensaje("Reserva confirmada");
        dto.setEmailDestino("cliente@hotel.com");
        dto.setEstado(estado);
        return dto;
    }
}