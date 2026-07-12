package com.hotel.msservicios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.msservicios.dto.ServicioRequestDTO;
import com.hotel.msservicios.dto.ServicioResponseDTO;
import com.hotel.msservicios.exception.ServicioNotFoundException;
import com.hotel.msservicios.service.ServicioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServicioController.class)
class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicioService servicioService;

    private ServicioResponseDTO crearResponseDePrueba() {
        return new ServicioResponseDTO(1L, "Spa", "Masajes relajantes", 15000.0, "wellness", true, true);
    }

    // ---------- GET /api/servicios ----------

    @Test
    void obtenerTodos_deberiaRetornar200YListaDeServicios() throws Exception {
        when(servicioService.obtenerTodos()).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Spa"));
    }

    @Test
    void obtenerTodos_deberiaRetornar200YListaVacia_cuandoNoHayServicios() throws Exception {
        when(servicioService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- GET /api/servicios/{id} ----------

    @Test
    void obtenerPorId_deberiaRetornar200_cuandoExiste() throws Exception {
        when(servicioService.obtenerPorId(1L)).thenReturn(crearResponseDePrueba());

        mockMvc.perform(get("/api/servicios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Spa"))
                .andExpect(jsonPath("$.precio").value(15000.0));
    }

    @Test
    void obtenerPorId_deberiaRetornar404_cuandoNoExiste() throws Exception {
        when(servicioService.obtenerPorId(99L))
                .thenThrow(new ServicioNotFoundException("Servicio no encontrado con id: 99"));

        mockMvc.perform(get("/api/servicios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Servicio no encontrado con id: 99"));
    }

    // ---------- GET /api/servicios/tipo/{tipo} ----------

    @Test
    void obtenerPorTipo_deberiaRetornar200YServiciosFiltrados() throws Exception {
        when(servicioService.obtenerPorTipo("wellness")).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/servicios/tipo/wellness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("wellness"));
    }

    // ---------- GET /api/servicios/disponibles ----------

    @Test
    void obtenerDisponibles_deberiaRetornar200() throws Exception {
        when(servicioService.obtenerDisponibles()).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/servicios/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponible").value(true));
    }

    // ---------- POST /api/servicios ----------

    @Test
    void crear_deberiaRetornar201_cuandoDatosValidos() throws Exception {
        ServicioRequestDTO request = new ServicioRequestDTO("Spa", "Masajes relajantes", 15000.0, "wellness", true);
        when(servicioService.crear(any(ServicioRequestDTO.class))).thenReturn(crearResponseDePrueba());

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Spa"));
    }

    @Test
    void crear_deberiaRetornar400_cuandoNombreVacio() throws Exception {
        ServicioRequestDTO request = new ServicioRequestDTO("", "Masajes relajantes", 15000.0, "wellness", true);

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists());

        verify(servicioService, never()).crear(any());
    }

    @Test
    void crear_deberiaRetornar400_cuandoPrecioMenorAlMinimo() throws Exception {
        ServicioRequestDTO request = new ServicioRequestDTO("Spa", "Masajes relajantes", 500.0, "wellness", true);

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precio").exists());
    }

    @Test
    void crear_deberiaRetornar400_cuandoNombreYaExiste() throws Exception {
        ServicioRequestDTO request = new ServicioRequestDTO("Spa", "Masajes relajantes", 15000.0, "wellness", true);
        when(servicioService.crear(any(ServicioRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un servicio con ese nombre"));

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ya existe un servicio con ese nombre"));
    }

    // ---------- PUT /api/servicios/{id} ----------

    @Test
    void actualizar_deberiaRetornar200_cuandoDatosValidos() throws Exception {
        ServicioRequestDTO request = new ServicioRequestDTO("Spa Premium", "Nueva desc", 20000.0, "wellness", false);
        ServicioResponseDTO response = new ServicioResponseDTO(1L, "Spa Premium", "Nueva desc", 20000.0, "wellness", false, true);

        when(servicioService.actualizar(eq(1L), any(ServicioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/servicios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Spa Premium"))
                .andExpect(jsonPath("$.disponible").value(false));
    }

    @Test
    void actualizar_deberiaRetornar404_cuandoNoExiste() throws Exception {
        ServicioRequestDTO request = new ServicioRequestDTO("Spa", "desc", 15000.0, "wellness", true);
        when(servicioService.actualizar(anyLong(), any(ServicioRequestDTO.class)))
                .thenThrow(new ServicioNotFoundException("Servicio no encontrado con id: 99"));

        mockMvc.perform(put("/api/servicios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE /api/servicios/{id} ----------

    @Test
    void eliminar_deberiaRetornar200_cuandoExiste() throws Exception {
        doNothing().when(servicioService).eliminar(1L);

        mockMvc.perform(delete("/api/servicios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Servicio eliminado correctamente"));

        verify(servicioService).eliminar(1L);
    }

    @Test
    void eliminar_deberiaRetornar404_cuandoNoExiste() throws Exception {
        doThrow(new ServicioNotFoundException("Servicio no encontrado con id: 99"))
                .when(servicioService).eliminar(99L);

        mockMvc.perform(delete("/api/servicios/99"))
                .andExpect(status().isNotFound());
    }
}