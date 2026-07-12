package com.hotel.msservicios.service;

import com.hotel.msservicios.cliente.HotelCliente;
import com.hotel.msservicios.dto.ServicioRequestDTO;
import com.hotel.msservicios.dto.ServicioResponseDTO;
import com.hotel.msservicios.exception.HotelInvalidoException;
import com.hotel.msservicios.exception.ServicioNotFoundException;
import com.hotel.msservicios.model.Servicio;
import com.hotel.msservicios.repository.ServicioRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private HotelCliente hotelCliente; // dependencia externa mockeada, no se llama a ms-hoteles real

    @InjectMocks
    private ServicioService servicioService;

    // Helper: crea un Servicio "de mentira" para usar en varios tests
    private Servicio crearServicioDePrueba() {
        return new Servicio(1L, 10L, "Spa", "Masajes relajantes", 15000.0, "wellness", true, true);
    }

    // Helper: simula una FeignException.NotFound (404 de ms-hoteles) sin depender
    // de la firma exacta del constructor de Feign, que varía entre versiones.
    private FeignException.NotFound notFoundDeMsHoteles() {
        return mock(FeignException.NotFound.class);
    }

    // ---------- obtenerPorId ----------

    @Test
    void obtenerPorId_deberiaRetornarDTO_cuandoExiste() {
        Servicio servicio = crearServicioDePrueba();
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));

        ServicioResponseDTO resultado = servicioService.obtenerPorId(1L);

        assertEquals("Spa", resultado.getNombre());
        assertEquals(15000.0, resultado.getPrecio());
        verify(servicioRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServicioNotFoundException.class,
                () -> servicioService.obtenerPorId(99L));
    }

    // ---------- obtenerTodos ----------

    @Test
    void obtenerTodos_deberiaRetornarSoloActivos() {
        Servicio activo = crearServicioDePrueba();
        when(servicioRepository.findByActivoTrue()).thenReturn(List.of(activo));

        List<ServicioResponseDTO> resultado = servicioService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("Spa", resultado.get(0).getNombre());
    }

    // ---------- obtenerPorHotel ----------

    @Test
    void obtenerPorHotel_deberiaRetornarServiciosDelHotel() {
        Servicio servicio = crearServicioDePrueba(); // hotelId = 10L
        when(servicioRepository.findByHotelIdAndActivoTrue(10L)).thenReturn(List.of(servicio));

        List<ServicioResponseDTO> resultado = servicioService.obtenerPorHotel(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getHotelId());
    }

    // ---------- crear ----------

    @Test
    void crear_deberiaGuardarYRetornarDTO_cuandoNombreNoExiste() {
        ServicioRequestDTO dto = new ServicioRequestDTO(10L, "Spa", "Masajes relajantes", 15000.0, "wellness", true);
        Servicio guardado = crearServicioDePrueba();

        when(servicioRepository.existsByNombre("Spa")).thenReturn(false);
        when(servicioRepository.save(any(Servicio.class))).thenReturn(guardado);

        ServicioResponseDTO resultado = servicioService.crear(dto);

        assertEquals("Spa", resultado.getNombre());
        assertThat(resultado.isActivo()).isTrue();
        verify(servicioRepository).save(any(Servicio.class));
        verify(hotelCliente, times(1)).obtenerHotelPorId(10L);
    }

    @Test
    void crear_deberiaLanzarExcepcion_cuandoNombreYaExiste() {
        ServicioRequestDTO dto = new ServicioRequestDTO(10L, "Spa", "desc", 15000.0, "wellness", true);
        when(servicioRepository.existsByNombre("Spa")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> servicioService.crear(dto));

        // Nunca debe llegar a guardar si el nombre ya existe
        verify(servicioRepository, never()).save(any());
        // Tampoco debe llegar a validar el hotel: el chequeo de nombre va primero
        verify(hotelCliente, never()).obtenerHotelPorId(any());
    }

    @Test
    void crear_deberiaLanzarHotelInvalido_cuandoHotelNoExisteEnMsHoteles() {
        ServicioRequestDTO dto = new ServicioRequestDTO(999L, "Spa", "desc", 15000.0, "wellness", true);

        when(servicioRepository.existsByNombre("Spa")).thenReturn(false);
        when(hotelCliente.obtenerHotelPorId(999L)).thenThrow(notFoundDeMsHoteles());

        assertThrows(HotelInvalidoException.class,
                () -> servicioService.crear(dto));

        verify(servicioRepository, never()).save(any());
    }

    // ---------- actualizar ----------

    @Test
    void actualizar_deberiaModificarCamposYGuardar_cuandoHotelIdNoCambia() {
        Servicio existente = crearServicioDePrueba(); // hotelId = 10L
        ServicioRequestDTO dto = new ServicioRequestDTO(10L, "Spa Premium", "Nueva desc", 20000.0, "wellness", false);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        ServicioResponseDTO resultado = servicioService.actualizar(1L, dto);

        assertEquals("Spa Premium", resultado.getNombre());
        assertEquals(20000.0, resultado.getPrecio());
        assertThat(resultado.isDisponible()).isFalse();
        // Como el hotelId no cambió, no debe volver a consultar ms-hoteles
        verify(hotelCliente, never()).obtenerHotelPorId(any());
    }

    @Test
    void actualizar_deberiaValidarHotel_cuandoHotelIdCambia() {
        Servicio existente = crearServicioDePrueba(); // hotelId = 10L
        ServicioRequestDTO dto = new ServicioRequestDTO(20L, "Spa Premium", "Nueva desc", 20000.0, "wellness", false);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(inv -> inv.getArgument(0));

        ServicioResponseDTO resultado = servicioService.actualizar(1L, dto);

        assertEquals(20L, resultado.getHotelId());
        verify(hotelCliente, times(1)).obtenerHotelPorId(20L);
    }

    @Test
    void actualizar_deberiaLanzarHotelInvalido_cuandoNuevoHotelNoExiste() {
        Servicio existente = crearServicioDePrueba(); // hotelId = 10L
        ServicioRequestDTO dto = new ServicioRequestDTO(999L, "Spa Premium", "Nueva desc", 20000.0, "wellness", false);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(hotelCliente.obtenerHotelPorId(999L)).thenThrow(notFoundDeMsHoteles());

        assertThrows(HotelInvalidoException.class,
                () -> servicioService.actualizar(1L, dto));

        verify(servicioRepository, never()).save(any());
    }

    @Test
    void actualizar_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(servicioRepository.findById(anyLong())).thenReturn(Optional.empty());
        ServicioRequestDTO dto = new ServicioRequestDTO(10L, "X", "Y", 1000.0, "tipo", true);

        assertThrows(ServicioNotFoundException.class,
                () -> servicioService.actualizar(1L, dto));
    }

    // ---------- eliminar ----------

    @Test
    void eliminar_deberiaMarcarComoInactivo_enVezDeBorrarFisicamente() {
        Servicio existente = crearServicioDePrueba();
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(existente));

        servicioService.eliminar(1L);

        assertThat(existente.isActivo()).isFalse();
        verify(servicioRepository).save(existente);
    }

    @Test
    void eliminar_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(servicioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ServicioNotFoundException.class,
                () -> servicioService.eliminar(1L));
    }
}