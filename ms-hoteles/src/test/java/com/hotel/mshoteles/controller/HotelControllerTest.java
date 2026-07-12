package com.hotel.mshoteles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.mshoteles.dto.HotelRequestDTO;
import com.hotel.mshoteles.dto.HotelResponseDTO;
import com.hotel.mshoteles.exception.HotelNotFoundException;
import com.hotel.mshoteles.service.HotelService;
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

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelService hotelService;

    private HotelResponseDTO crearResponseDePrueba() {
        return new HotelResponseDTO(1L, "Hotel Plaza", "Av. Siempre Viva 123",
                "Santiago", "Chile", 4, "+56912345678", "contacto@hotelplaza.cl", true);
    }

    @Test
    void obtenerTodos_deberiaRetornar200YListaDeHoteles() throws Exception {
        when(hotelService.obtenerTodos()).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/hoteles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Hotel Plaza"));
    }

    @Test
    void obtenerPorId_deberiaRetornar200_cuandoExiste() throws Exception {
        when(hotelService.obtenerPorId(1L)).thenReturn(crearResponseDePrueba());

        mockMvc.perform(get("/api/hoteles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Hotel Plaza"))
                .andExpect(jsonPath("$.ciudad").value("Santiago"));
    }

    @Test
    void obtenerPorId_deberiaRetornar404_cuandoNoExiste() throws Exception {
        when(hotelService.obtenerPorId(99L))
                .thenThrow(new HotelNotFoundException("Hotel no encontrado con id: 99"));

        mockMvc.perform(get("/api/hoteles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Hotel no encontrado con id: 99"));
    }

    @Test
    void obtenerPorCiudad_deberiaRetornar200YHotelesFiltrados() throws Exception {
        when(hotelService.obtenerPorCiudad("Santiago")).thenReturn(List.of(crearResponseDePrueba()));

        mockMvc.perform(get("/api/hoteles/ciudad/Santiago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ciudad").value("Santiago"));
    }

    @Test
    void crear_deberiaRetornar201_cuandoDatosValidos() throws Exception {
        HotelRequestDTO request = new HotelRequestDTO("Hotel Plaza", "Av. Siempre Viva 123",
                "Santiago", "Chile", 4, "+56912345678", "contacto@hotelplaza.cl");

        when(hotelService.crear(any(HotelRequestDTO.class))).thenReturn(crearResponseDePrueba());

        mockMvc.perform(post("/api/hoteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Hotel Plaza"));
    }

    @Test
    void crear_deberiaRetornar400_cuandoNombreMuyCorto() throws Exception {
        HotelRequestDTO request = new HotelRequestDTO("Ab", "Av. Siempre Viva 123",
                "Santiago", "Chile", 4, "+56912345678", "contacto@hotelplaza.cl");

        mockMvc.perform(post("/api/hoteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists());

        verify(hotelService, never()).crear(any());
    }

    @Test
    void crear_deberiaRetornar400_cuandoEstrellasFueraDeRango() throws Exception {
        HotelRequestDTO request = new HotelRequestDTO("Hotel Plaza", "Av. Siempre Viva 123",
                "Santiago", "Chile", 6, "+56912345678", "contacto@hotelplaza.cl");

        mockMvc.perform(post("/api/hoteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estrellas").exists());
    }

    @Test
    void crear_deberiaRetornar400_cuandoCorreoInvalido() throws Exception {
        HotelRequestDTO request = new HotelRequestDTO("Hotel Plaza", "Av. Siempre Viva 123",
                "Santiago", "Chile", 4, "+56912345678", "no-es-un-correo");

        mockMvc.perform(post("/api/hoteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.correo").exists());
    }

    @Test
    void crear_deberiaRetornar400_cuandoCorreoYaExiste() throws Exception {
        HotelRequestDTO request = new HotelRequestDTO("Hotel Plaza", "Av. Siempre Viva 123",
                "Santiago", "Chile", 4, "+56912345678", "contacto@hotelplaza.cl");

        when(hotelService.crear(any(HotelRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un hotel con el correo: contacto@hotelplaza.cl"));

        mockMvc.perform(post("/api/hoteles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ya existe un hotel con el correo: contacto@hotelplaza.cl"));
    }

    @Test
    void actualizar_deberiaRetornar200_cuandoDatosValidos() throws Exception {
        HotelRequestDTO request = new HotelRequestDTO("Hotel Plaza Renovado", "Nueva Direccion 456",
                "Valparaiso", "Chile", 5, "+56987654321", "nuevo@hotelplaza.cl");
        HotelResponseDTO response = new HotelResponseDTO(1L, "Hotel Plaza Renovado", "Nueva Direccion 456",
                "Valparaiso", "Chile", 5, "+56987654321", "nuevo@hotelplaza.cl", true);

        when(hotelService.actualizar(eq(1L), any(HotelRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/hoteles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciudad").value("Valparaiso"))
                .andExpect(jsonPath("$.estrellas").value(5));
    }

    @Test
    void actualizar_deberiaRetornar404_cuandoNoExiste() throws Exception {
        HotelRequestDTO request = new HotelRequestDTO("Hotel Plaza", "Av. Siempre Viva 123",
                "Santiago", "Chile", 4, "+56912345678", "contacto@hotelplaza.cl");

        when(hotelService.actualizar(anyLong(), any(HotelRequestDTO.class)))
                .thenThrow(new HotelNotFoundException("Hotel no encontrado con id: 99"));

        mockMvc.perform(put("/api/hoteles/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_deberiaRetornar200_cuandoExiste() throws Exception {
        doNothing().when(hotelService).eliminar(1L);

        mockMvc.perform(delete("/api/hoteles/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hotel eliminado correctamente"));

        verify(hotelService).eliminar(1L);
    }

    @Test
    void eliminar_deberiaRetornar404_cuandoNoExiste() throws Exception {
        doThrow(new HotelNotFoundException("Hotel no encontrado con id: 99"))
                .when(hotelService).eliminar(99L);

        mockMvc.perform(delete("/api/hoteles/99"))
                .andExpect(status().isNotFound());
    }
}