package com.hotel.mshoteles.service;

import com.hotel.mshoteles.dto.HotelRequestDTO;
import com.hotel.mshoteles.dto.HotelResponseDTO;
import com.hotel.mshoteles.exception.HotelNotFoundException;
import com.hotel.mshoteles.model.Hotel;
import com.hotel.mshoteles.repository.HotelRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de HotelService.
 * Se mockea HotelRepository para aislar la lógica de negocio de la capa de persistencia.
 * Estructura: Given (arrange) - When (act) - Then (assert)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HotelService - pruebas unitarias")
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;
    private HotelRequestDTO hotelRequestDTO;

    @BeforeEach
    void setUp() {
        // Given: un hotel base reutilizable en varios tests
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Hotel Plaza");
        hotel.setDireccion("Av. Siempre Viva 123");
        hotel.setCiudad("Santiago");
        hotel.setPais("Chile");
        hotel.setEstrellas(4);
        hotel.setTelefono("+56912345678");
        hotel.setCorreo("contacto@hotelplaza.cl");
        hotel.setActivo(true);

        hotelRequestDTO = new HotelRequestDTO(
                "Hotel Plaza",
                "Av. Siempre Viva 123",
                "Santiago",
                "Chile",
                4,
                "+56912345678",
                "contacto@hotelplaza.cl"
        );
    }

    // ---------- obtenerTodos ----------

    @Test
    @DisplayName("obtenerTodos() debe retornar solo hoteles activos mapeados a DTO")
    void obtenerTodos_debeRetornarListaDeHotelesActivos() {
        // Given
        when(hotelRepository.findByActivoTrue()).thenReturn(List.of(hotel));

        // When
        List<HotelResponseDTO> resultado = hotelService.obtenerTodos();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Hotel Plaza", resultado.get(0).getNombre());
        verify(hotelRepository, times(1)).findByActivoTrue();
    }

    @Test
    @DisplayName("obtenerTodos() debe retornar lista vacía cuando no hay hoteles activos")
    void obtenerTodos_debeRetornarListaVaciaSiNoHayHoteles() {
        // Given
        when(hotelRepository.findByActivoTrue()).thenReturn(List.of());

        // When
        List<HotelResponseDTO> resultado = hotelService.obtenerTodos();

        // Then
        assertTrue(resultado.isEmpty());
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId() debe retornar el hotel cuando existe")
    void obtenerPorId_debeRetornarHotelExistente() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        // When
        HotelResponseDTO resultado = hotelService.obtenerPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Hotel Plaza", resultado.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId() debe lanzar HotelNotFoundException cuando el id no existe")
    void obtenerPorId_debeLanzarExcepcionSiNoExiste() {
        // Given
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        HotelNotFoundException ex = assertThrows(
                HotelNotFoundException.class,
                () -> hotelService.obtenerPorId(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- obtenerPorCiudad ----------

    @Test
    @DisplayName("obtenerPorCiudad() debe filtrar hoteles activos por ciudad")
    void obtenerPorCiudad_debeRetornarHotelesDeLaCiudad() {
        // Given
        when(hotelRepository.findByCiudadAndActivoTrue("Santiago"))
                .thenReturn(List.of(hotel));

        // When
        List<HotelResponseDTO> resultado = hotelService.obtenerPorCiudad("Santiago");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Santiago", resultado.get(0).getCiudad());
    }

    // ---------- crear ----------

    @Test
    @DisplayName("crear() debe guardar el hotel cuando el correo no está registrado")
    void crear_debeGuardarHotelSiCorreoNoExiste() {
        // Given
        when(hotelRepository.existsByCorreo(anyString())).thenReturn(false);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        // When
        HotelResponseDTO resultado = hotelService.crear(hotelRequestDTO);

        // Then
        assertNotNull(resultado);
        assertEquals("Hotel Plaza", resultado.getNombre());
        assertTrue(resultado.isActivo());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    @DisplayName("crear() debe lanzar IllegalArgumentException si el correo ya existe")
    void crear_debeLanzarExcepcionSiCorreoYaExiste() {
        // Given
        when(hotelRepository.existsByCorreo(anyString())).thenReturn(true);

        // When & Then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> hotelService.crear(hotelRequestDTO)
        );
        assertTrue(ex.getMessage().contains(hotelRequestDTO.getCorreo()));

        // No se debe intentar guardar si la validación falla
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("actualizar() debe modificar y guardar los datos del hotel existente")
    void actualizar_debeActualizarHotelExistente() {
        // Given
        HotelRequestDTO nuevosDatos = new HotelRequestDTO(
                "Hotel Plaza Renovado",
                "Nueva Direccion 456",
                "Valparaiso",
                "Chile",
                5,
                "+56987654321",
                "nuevo@hotelplaza.cl"
        );

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        HotelResponseDTO resultado = hotelService.actualizar(1L, nuevosDatos);

        // Then
        assertEquals("Hotel Plaza Renovado", resultado.getNombre());
        assertEquals("Valparaiso", resultado.getCiudad());
        assertEquals(5, resultado.getEstrellas());
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    @DisplayName("actualizar() debe lanzar HotelNotFoundException si el hotel no existe")
    void actualizar_debeLanzarExcepcionSiNoExiste() {
        // Given
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                HotelNotFoundException.class,
                () -> hotelService.actualizar(99L, hotelRequestDTO)
        );
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    // ---------- eliminar (soft delete) ----------

    @Test
    @DisplayName("eliminar() debe marcar el hotel como inactivo en lugar de borrarlo")
    void eliminar_debeDesactivarHotel() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        // When
        hotelService.eliminar(1L);

        // Then
        assertFalse(hotel.isActivo());
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    @DisplayName("eliminar() debe lanzar HotelNotFoundException si el hotel no existe")
    void eliminar_debeLanzarExcepcionSiNoExiste() {
        // Given
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                HotelNotFoundException.class,
                () -> hotelService.eliminar(99L)
        );
        verify(hotelRepository, never()).save(any(Hotel.class));
    }
}
