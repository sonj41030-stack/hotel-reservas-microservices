package com.hotel.mshabitaciones.service;

import com.hotel.mshabitaciones.cliente.HotelCliente;
import com.hotel.mshabitaciones.dto.HabitacionRequestDTO;
import com.hotel.mshabitaciones.dto.HabitacionResponseDTO;
import com.hotel.mshabitaciones.exception.HabitacionNotFoundException;
import com.hotel.mshabitaciones.exception.HotelInvalidoException;
import com.hotel.mshabitaciones.model.Habitacion;
import com.hotel.mshabitaciones.repository.HabitacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HabitacionService - pruebas unitarias")
class HabitacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private HotelCliente hotelCliente; // dependencia externa mockeada, no se llama a ms-hoteles real

    @InjectMocks
    private HabitacionService habitacionService;

    private Habitacion habitacion;
    private HabitacionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        habitacion = new Habitacion();
        habitacion.setId(1L);
        habitacion.setHotelId(10L);
        habitacion.setTipo("Doble");
        habitacion.setCapacidad(2);
        habitacion.setPrecio(45000.0);
        habitacion.setDisponible(true);
        habitacion.setPermiteMascotas(false);
        habitacion.setActivo(true);

        requestDTO = new HabitacionRequestDTO(10L, "Doble", 2, 45000.0, true, false);
    }

    @Test
    @DisplayName("crear() debe guardar la habitación cuando el hotel existe")
    void crear_debeGuardarSiHotelExiste() {

        when(hotelCliente.existeHotel(10L)).thenReturn(true);
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);


        HabitacionResponseDTO resultado = habitacionService.crear(requestDTO);


        assertNotNull(resultado);
        assertEquals("Doble", resultado.getTipo());
        verify(hotelCliente, times(1)).existeHotel(10L);
        verify(habitacionRepository, times(1)).save(any(Habitacion.class));
    }

    @Test
    @DisplayName("crear() debe lanzar HotelInvalidoException si el hotel NO existe en ms-hoteles")
    void crear_debeLanzarExcepcionSiHotelNoExiste() {

        when(hotelCliente.existeHotel(10L)).thenReturn(false);

        assertThrows(
                HotelInvalidoException.class,
                () -> habitacionService.crear(requestDTO)
        );

        verify(habitacionRepository, never()).save(any(Habitacion.class));
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar HabitacionNotFoundException si no existe")
    void obtenerPorId_debeLanzarExcepcionSiNoExiste() {

        when(habitacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                HabitacionNotFoundException.class,
                () -> habitacionService.obtenerPorId(99L)
        );
    }

    @Test
    @DisplayName("obtenerDisponibles() debe retornar solo habitaciones disponibles")
    void obtenerDisponibles_debeRetornarListaFiltrada() {

        when(habitacionRepository.findByDisponibleTrue()).thenReturn(List.of(habitacion));

        List<HabitacionResponseDTO> resultado = habitacionService.obtenerDisponibles();


        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isDisponible());
    }

    @Test
    @DisplayName("eliminar() debe desactivar la habitación (soft delete)")
    void eliminar_debeDesactivarHabitacion() {

        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        habitacionService.eliminar(1L);

        assertFalse(habitacion.isActivo());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    @DisplayName("eliminar() debe lanzar HabitacionNotFoundException si no existe")
    void eliminar_debeLanzarExcepcionSiNoExiste() {

        when(habitacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                HabitacionNotFoundException.class,
                () -> habitacionService.eliminar(99L)
        );

        verify(habitacionRepository, never()).save(any(Habitacion.class));
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar todas las habitaciones activas")
    void obtenerTodos_debeRetornarListaCompleta() {

        when(habitacionRepository.findByActivoTrue()).thenReturn(List.of(habitacion));

        List<HabitacionResponseDTO> resultado = habitacionService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("Doble", resultado.get(0).getTipo());
    }

    @Test
    @DisplayName("obtenerPorId() debe retornar la habitación cuando existe")
    void obtenerPorId_debeRetornarHabitacionCuandoExiste() {

        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacion));

        HabitacionResponseDTO resultado = habitacionService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Doble", resultado.getTipo());
    }

    @Test
    @DisplayName("obtenerPorHotel() debe retornar habitaciones del hotel indicado")
    void obtenerPorHotel_debeRetornarHabitacionesDelHotel() {

        when(habitacionRepository.findByHotelIdAndActivoTrue(10L)).thenReturn(List.of(habitacion));

        List<HabitacionResponseDTO> resultado = habitacionService.obtenerPorHotel(10L);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getHotelId());
    }

    @Test
    @DisplayName("obtenerPermiteMascotas() debe retornar solo habitaciones que permiten mascotas")
    void obtenerPermiteMascotas_debeRetornarListaFiltrada() {

        habitacion.setPermiteMascotas(true);
        when(habitacionRepository.findByPermiteMascotasTrue()).thenReturn(List.of(habitacion));

        List<HabitacionResponseDTO> resultado = habitacionService.obtenerPermiteMascotas();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isPermiteMascotas());
    }

    @Test
    @DisplayName("actualizar() debe actualizar la habitación cuando el hotelId no cambia (sin validar en ms-hoteles)")
    void actualizar_debeActualizarSinValidarHotelSiHotelIdNoCambia() {

        HabitacionRequestDTO dto = new HabitacionRequestDTO(10L, "Suite", 4, 60000.0, false, true);

        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        HabitacionResponseDTO resultado = habitacionService.actualizar(1L, dto);

        assertEquals("Suite", resultado.getTipo());
        verify(hotelCliente, never()).existeHotel(any());
    }

    @Test
    @DisplayName("actualizar() debe validar en ms-hoteles y actualizar si el nuevo hotelId existe")
    void actualizar_debeValidarYActualizarSiNuevoHotelExiste() {

        HabitacionRequestDTO dto = new HabitacionRequestDTO(20L, "Suite", 4, 60000.0, false, true);

        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacion));
        when(hotelCliente.existeHotel(20L)).thenReturn(true);
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        HabitacionResponseDTO resultado = habitacionService.actualizar(1L, dto);

        assertEquals("Suite", resultado.getTipo());
        verify(hotelCliente, times(1)).existeHotel(20L);
    }

    @Test
    @DisplayName("actualizar() debe lanzar HotelInvalidoException si el nuevo hotelId no existe")
    void actualizar_debeLanzarExcepcionSiNuevoHotelNoExiste() {

        HabitacionRequestDTO dto = new HabitacionRequestDTO(20L, "Suite", 4, 60000.0, false, true);

        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacion));
        when(hotelCliente.existeHotel(20L)).thenReturn(false);

        assertThrows(
                HotelInvalidoException.class,
                () -> habitacionService.actualizar(1L, dto)
        );

        verify(habitacionRepository, never()).save(any(Habitacion.class));
    }

    @Test
    @DisplayName("actualizar() debe lanzar HabitacionNotFoundException si la habitación no existe")
    void actualizar_debeLanzarExcepcionSiNoExiste() {

        HabitacionRequestDTO dto = new HabitacionRequestDTO(10L, "Suite", 4, 60000.0, false, true);

        when(habitacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                HabitacionNotFoundException.class,
                () -> habitacionService.actualizar(99L, dto)
        );
    }
}
