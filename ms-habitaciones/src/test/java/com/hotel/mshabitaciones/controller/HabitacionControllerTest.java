package com.hotel.mshabitaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.mshabitaciones.dto.HabitacionRequestDTO;
import com.hotel.mshabitaciones.dto.HabitacionResponseDTO;
import com.hotel.mshabitaciones.exception.HabitacionNotFoundException;
import com.hotel.mshabitaciones.exception.HotelInvalidoException;
import com.hotel.mshabitaciones.service.HabitacionService;
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

@WebMvcTest(HabitacionController.class)
class HabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HabitacionService habitacionService;

    private HabitacionResponseDTO crearResponseDePrueba() {
        return new HabitacionResponseDTO(1L, 10L, "Doble", 2, 45000.0, true, false, true);
    }

    @Test
    void obtenerTodos_deberiaRetornar200YListaDeHabitaciones() throws Exception {
        when(habitacionService.obtenerTodos()).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("Doble"));
    }

    @Test
    void obtenerPorId_deberiaRetornar200_cuandoExiste() throws Exception {
        when(habitacionService.obtenerPorId(1L)).thenReturn(crearResponseDePrueba());

        mockMvc.perform(get("/api/habitaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Doble"))
                .andExpect(jsonPath("$.precio").value(45000.0));
    }

    @Test
    void obtenerPorId_deberiaRetornar404_cuandoNoExiste() throws Exception {
        when(habitacionService.obtenerPorId(99L))
                .thenThrow(new HabitacionNotFoundException("Habitacion no encontrada con id: 99"));

        mockMvc.perform(get("/api/habitaciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Habitacion no encontrada con id: 99"));
    }

    @Test
    void obtenerPorHotel_deberiaRetornar200YHabitacionesDelHotel() throws Exception {
        when(habitacionService.obtenerPorHotel(10L)).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/habitaciones/hotel/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hotelId").value(10));
    }

    @Test
    void obtenerDisponibles_deberiaRetornar200() throws Exception {
        when(habitacionService.obtenerDisponibles()).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/habitaciones/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponible").value(true));
    }

    @Test
    void obtenerPermiteMascotas_deberiaRetornar200() throws Exception {
        HabitacionResponseDTO conMascotas = new HabitacionResponseDTO(2L, 10L, "Suite", 4, 60000.0, true, true, true);
        when(habitacionService.obtenerPermiteMascotas()).thenReturn(List.of(conMascotas));

        mockMvc.perform(get("/api/habitaciones/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].permiteMascotas").value(true));
    }

    @Test
    void crear_deberiaRetornar201_cuandoDatosValidos() throws Exception {
        HabitacionRequestDTO request = new HabitacionRequestDTO(10L, "Doble", 2, 45000.0, true, false);
        when(habitacionService.crear(any(HabitacionRequestDTO.class))).thenReturn(crearResponseDePrueba());

        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("Doble"));
    }

    @Test
    void crear_deberiaRetornar400_cuandoTipoVacio() throws Exception {
        HabitacionRequestDTO request = new HabitacionRequestDTO(10L, "", 2, 45000.0, true, false);

        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipo").exists());

        verify(habitacionService, never()).crear(any());
    }

    @Test
    void crear_deberiaRetornar400_cuandoPrecioMenorAlMinimo() throws Exception {
        HabitacionRequestDTO request = new HabitacionRequestDTO(10L, "Doble", 2, 20000.0, true, false);

        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precio").exists());
    }

    @Test
    void crear_deberiaRetornar400_cuandoHotelIdNoExiste() throws Exception {
        // Test clave: confirma el fix del bug de HotelInvalidoException.
        // Antes del fix, esto devolvia 500 con mensaje generico.
        HabitacionRequestDTO request = new HabitacionRequestDTO(999L, "Doble", 2, 45000.0, true, false);

        when(habitacionService.crear(any(HabitacionRequestDTO.class)))
                .thenThrow(new HotelInvalidoException("No existe un hotel con id: 999"));

        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No existe un hotel con id: 999"));
    }

    @Test
    void actualizar_deberiaRetornar200_cuandoDatosValidos() throws Exception {
        HabitacionRequestDTO request = new HabitacionRequestDTO(10L, "Suite", 4, 60000.0, false, true);
        HabitacionResponseDTO response = new HabitacionResponseDTO(1L, 10L, "Suite", 4, 60000.0, false, true, true);

        when(habitacionService.actualizar(eq(1L), any(HabitacionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/habitaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Suite"))
                .andExpect(jsonPath("$.permiteMascotas").value(true));
    }

    @Test
    void actualizar_deberiaRetornar404_cuandoNoExiste() throws Exception {
        HabitacionRequestDTO request = new HabitacionRequestDTO(10L, "Doble", 2, 45000.0, true, false);

        when(habitacionService.actualizar(anyLong(), any(HabitacionRequestDTO.class)))
                .thenThrow(new HabitacionNotFoundException("Habitacion no encontrada con id: 99"));

        mockMvc.perform(put("/api/habitaciones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_deberiaRetornar400_cuandoNuevoHotelIdNoExiste() throws Exception {
        HabitacionRequestDTO request = new HabitacionRequestDTO(999L, "Doble", 2, 45000.0, true, false);

        when(habitacionService.actualizar(eq(1L), any(HabitacionRequestDTO.class)))
                .thenThrow(new HotelInvalidoException("No existe un hotel con id: 999"));

        mockMvc.perform(put("/api/habitaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No existe un hotel con id: 999"));
    }

    @Test
    void eliminar_deberiaRetornar200_cuandoExiste() throws Exception {
        doNothing().when(habitacionService).eliminar(1L);

        mockMvc.perform(delete("/api/habitaciones/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Habitación eliminada correctamente"));

        verify(habitacionService).eliminar(1L);
    }

    @Test
    void eliminar_deberiaRetornar404_cuandoNoExiste() throws Exception {
        doThrow(new HabitacionNotFoundException("Habitacion no encontrada con id: 99"))
                .when(habitacionService).eliminar(99L);

        mockMvc.perform(delete("/api/habitaciones/99"))
                .andExpect(status().isNotFound());
    }
}