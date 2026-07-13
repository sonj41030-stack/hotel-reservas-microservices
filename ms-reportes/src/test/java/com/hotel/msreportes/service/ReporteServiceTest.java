package com.hotel.msreportes.service;

import com.hotel.msreportes.client.HotelClient;
import com.hotel.msreportes.dto.ReporteDTO;
import com.hotel.msreportes.model.Reporte;
import com.hotel.msreportes.model.TiposReporte;
import com.hotel.msreportes.repository.ReporteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests a nivel de SERVICIO para ReporteService.
 *
 * @ExtendWith(MockitoExtension.class) → solo Mockito puro, sin Spring.
 * @InjectMocks → crea el servicio real e inyecta los mocks.
 * @Mock → simula repositorio y cliente Feign sin BD ni HTTP real.
 *
 * Patrón de cada test:
 *   1. Configurar mock (when...thenReturn)
 *   2. Llamar al método del servicio
 *   3. Verificar resultado (assert...)
 */
@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    // Servicio REAL con dependencias mockeadas inyectadas
    @InjectMocks
    private ReporteService reporteService;

    // Mock del repositorio: simula la BD sin conectarse
    @Mock
    private ReporteRepository reporteRepository;

    // Mock del cliente Feign: simula la llamada a ms-hoteles
    @Mock
    private HotelClient hotelClient;

    // ─────────────────────────────────────────────────────────────────
    // TEST 1: listarTodos() → debe retornar lista no vacía
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testListarTodos() {
        // 1. Configurar mock: findAll() devuelve un reporte de prueba
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.OCUPACION);
        when(reporteRepository.findAll()).thenReturn(List.of(reporte));

        // 2. Llamar al servicio
        List<Reporte> resultado = reporteService.listarTodos();

        // 3. Verificar lista no nula con un elemento
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 2: obtenerPorId() → debe retornar el reporte correcto
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorId() {
        // 1. Cuando busque id=1, devolver el reporte
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.INGRESOS);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));

        // 2. Llamar al servicio
        Reporte resultado = reporteService.obtenerPorId(1L);

        // 3. Verificar datos correctos
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(TiposReporte.INGRESOS, resultado.getTipo());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 3: obtenerPorId() con id inexistente → lanza excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorIdNoExiste() {
        // 1. id=99 no existe en la BD
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        // 2 y 3. Debe lanzar RuntimeException con el id en el mensaje
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reporteService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 4: generar() → debe guardar y retornar el reporte
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testGenerar() {
        // 1. DTO de prueba con fechas válidas
        ReporteDTO dto = crearReporteDTO(
                TiposReporte.RESERVAS,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );
        dto.setHotelId(1L);

        // HotelClient no lanza excepción → hotel existe
        when(hotelClient.obtenerHotel(1L)).thenReturn(null);

        // Repositorio devuelve el reporte guardado
        Reporte guardado = crearReporteDePrueba(1L, TiposReporte.RESERVAS);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(guardado);

        // 2. Llamar al servicio
        Reporte resultado = reporteService.generar(dto);

        // 3. Verificar resultado y que se guardó
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 5: generar() con fechaInicio posterior a fechaFin → lanza excepción
    //         (Regla de negocio: fechaInicio no puede ser después de fechaFin)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testGenerarFechaInvalida() {
        // 1. fechaInicio DESPUÉS de fechaFin → inválido
        ReporteDTO dto = crearReporteDTO(
                TiposReporte.GENERAL,
                LocalDate.of(2025, 12, 31),  // inicio
                LocalDate.of(2025, 1, 1)     // fin (anterior al inicio)
        );

        // 2 y 3. Debe rechazarlo
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reporteService.generar(dto));

        assertTrue(ex.getMessage().contains("fecha de inicio"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 6: generar() con porcentaje de ocupación inválido → lanza excepción
    //         (Regla de negocio: porcentaje debe estar entre 0 y 100)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testGenerarPorcentajeInvalido() {
        // 1. Porcentaje de ocupación = 150 → inválido
        ReporteDTO dto = crearReporteDTO(
                TiposReporte.OCUPACION,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );
        dto.setPorcentajeOcupacion(150.0);

        // 2 y 3.
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reporteService.generar(dto));

        assertTrue(ex.getMessage().contains("porcentaje"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 7: generar() con hotel inexistente → lanza excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testGenerarHotelNoExiste() {
        // 1. HotelClient lanza excepción → hotel no existe
        ReporteDTO dto = crearReporteDTO(
                TiposReporte.INGRESOS,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );
        dto.setHotelId(99L);

        when(hotelClient.obtenerHotel(99L))
                .thenThrow(new RuntimeException("Hotel no encontrado"));

        // 2 y 3.
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reporteService.generar(dto));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 8: actualizar() → debe modificar y guardar el reporte
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testActualizar() {
        // 1. El reporte existe
        Reporte existente = crearReporteDePrueba(1L, TiposReporte.OCUPACION);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(existente));

        ReporteDTO dto = crearReporteDTO(
                TiposReporte.INGRESOS,
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28)
        );

        Reporte actualizado = crearReporteDePrueba(1L, TiposReporte.INGRESOS);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(actualizado);

        // 2. Llamar al servicio
        Reporte resultado = reporteService.actualizar(1L, dto);

        // 3. Verificar que se guardó con el tipo actualizado
        assertNotNull(resultado);
        assertEquals(TiposReporte.INGRESOS, resultado.getTipo());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 9: eliminar() → debe llamar a repository.delete() una vez
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testEliminar() {
        // 1. El reporte existe
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.GENERAL);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        doNothing().when(reporteRepository).delete(reporte);

        // 2. Llamar al servicio
        reporteService.eliminar(1L);

        // 3. Verificar que delete() fue llamado exactamente una vez
        verify(reporteRepository, times(1)).delete(reporte);
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 10: obtenerPorTipo() → debe retornar reportes del tipo indicado
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorTipo() {
        // 1. Repositorio devuelve reportes de tipo OCUPACION
        Reporte reporte = crearReporteDePrueba(1L, TiposReporte.OCUPACION);
        when(reporteRepository.findByTipo(TiposReporte.OCUPACION)).thenReturn(List.of(reporte));

        // 2. Llamar al servicio
        List<Reporte> resultado = reporteService.obtenerPorTipo(TiposReporte.OCUPACION);

        // 3. Verificar resultado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(TiposReporte.OCUPACION, resultado.get(0).getTipo());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 11: obtenerPorRango() con fechas inválidas → lanza excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorRangoFechaInvalida() {
        // inicio después de fin → inválido
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reporteService.obtenerPorRango(
                        LocalDate.of(2025, 12, 31),
                        LocalDate.of(2025, 1, 1)
                ));

        assertTrue(ex.getMessage().contains("fecha de inicio"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 12: calcularIngresosTotales() → debe retornar 0.0 si no hay reportes
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCalcularIngresosTotalesNull() {
        // 1. El repositorio devuelve null (sin reportes en el periodo)
        when(reporteRepository.calcularIngresosTotales(any(), any(), any()))
                .thenReturn(null);

        // 2. Llamar al servicio
        Double resultado = reporteService.calcularIngresosTotales(
                1L,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );

        // 3. Debe retornar 0.0 en vez de null
        assertEquals(0.0, resultado);
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: crea un Reporte de prueba reutilizable
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
        dto.setObservaciones("DTO de prueba");
        return dto;
    }
}