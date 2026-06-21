package com.hotel.mshousekeeping.service;

import com.hotel.mshousekeeping.client.HabitacionClient;
import com.hotel.mshousekeeping.dto.TareaDTO;
import com.hotel.mshousekeeping.model.EstadoTarea;
import com.hotel.mshousekeeping.model.TareaHousekeeping;
import com.hotel.mshousekeeping.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests a nivel de SERVICIO para TareaService.
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
class HouseKeepingServiceTest {

    // Servicio REAL con dependencias mockeadas inyectadas
    @InjectMocks
    private TareaService tareaService;

    // Mock del repositorio: simula la BD sin conectarse
    @Mock
    private TareaRepository tareaRepository;

    // Mock del cliente Feign: simula la llamada a ms-habitaciones
    @Mock
    private HabitacionClient habitacionClient;

    // ─────────────────────────────────────────────────────────────────
    // TEST 1: listarTodas() → debe retornar lista no vacía
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testListarTodas() {
        // 1. Configurar mock: findAll() devuelve una tarea de prueba
        TareaHousekeeping tarea = crearTareaDePrueba(1L, EstadoTarea.PENDIENTE);
        when(tareaRepository.findAll()).thenReturn(List.of(tarea));

        // 2. Llamar al servicio
        List<TareaHousekeeping> resultado = tareaService.listarTodas();

        // 3. Verificar lista no nula con un elemento
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 2: obtenerPorId() → debe retornar la tarea correcta
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorId() {
        // 1. Cuando busque id=1, devolver la tarea
        TareaHousekeeping tarea = crearTareaDePrueba(1L, EstadoTarea.PENDIENTE);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        // 2. Llamar al servicio
        TareaHousekeeping resultado = tareaService.obtenerPorId(1L);

        // 3. Verificar datos correctos
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(EstadoTarea.PENDIENTE, resultado.getEstado());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 3: obtenerPorId() con id inexistente → lanza excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorIdNoExiste() {
        // 1. id=99 no existe en la BD
        when(tareaRepository.findById(99L)).thenReturn(Optional.empty());

        // 2 y 3. Debe lanzar RuntimeException con el id en el mensaje
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tareaService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 4: crear() → debe guardar y retornar la tarea
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCrear() {
        // 1. DTO de prueba con datos válidos
        TareaDTO dto = crearTareaDTO("LIMPIEZA", "ALTA");
        dto.setHabitacionId(10L);

        // HabitacionClient no lanza excepción → habitación existe
        when(habitacionClient.obtenerHabitacion(10L)).thenReturn(new Object());

        // Repositorio devuelve la tarea guardada
        TareaHousekeeping guardada = crearTareaDePrueba(1L, EstadoTarea.PENDIENTE);
        when(tareaRepository.save(any(TareaHousekeeping.class))).thenReturn(guardada);

        // 2. Llamar al servicio
        TareaHousekeeping resultado = tareaService.crear(dto);

        // 3. Verificar resultado y que se guardó
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(tareaRepository, times(1)).save(any(TareaHousekeeping.class));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 5: crear() con habitación inexistente → lanza excepción
    //         (Feign lanza error → NO se guarda nada en BD)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCrearHabitacionNoExiste() {
        // 1. HabitacionClient lanza excepción → habitación no existe
        TareaDTO dto = crearTareaDTO("LIMPIEZA", "MEDIA");
        dto.setHabitacionId(99L);

        when(habitacionClient.obtenerHabitacion(99L))
                .thenThrow(new RuntimeException("Habitación no encontrada"));

        // 2 y 3. Debe rechazarlo y NO guardar en BD
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tareaService.crear(dto));

        assertTrue(ex.getMessage().contains("99"));
        verify(tareaRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 6: cambiarEstado() → debe actualizar el estado correctamente
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCambiarEstado() {
        // 1. Tarea en estado PENDIENTE
        TareaHousekeeping tarea = crearTareaDePrueba(1L, EstadoTarea.PENDIENTE);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        TareaHousekeeping actualizada = crearTareaDePrueba(1L, EstadoTarea.EN_PROCESO);
        when(tareaRepository.save(any(TareaHousekeeping.class))).thenReturn(actualizada);

        // 2. Llamar al servicio
        TareaHousekeeping resultado = tareaService.cambiarEstado(1L, "EN_PROCESO");

        // 3. Verificar que el estado cambió
        assertNotNull(resultado);
        assertEquals(EstadoTarea.EN_PROCESO, resultado.getEstado());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 7: cambiarEstado() con tarea COMPLETADA → lanza excepción
    //         (Regla de negocio: no se puede modificar una tarea completada)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCambiarEstadoTareaCompletada() {
        // 1. Tarea ya COMPLETADA
        TareaHousekeeping tarea = crearTareaDePrueba(1L, EstadoTarea.COMPLETADA);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        // 2 y 3. Debe rechazarlo con mensaje que mencione COMPLETADA
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tareaService.cambiarEstado(1L, "PENDIENTE"));

        assertTrue(ex.getMessage().contains("COMPLETADA"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 8: asignarEmpleado() → debe asignar empleado y cambiar a EN_PROCESO
    //         (Regla de negocio: al asignar empleado la tarea pasa a EN_PROCESO)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testAsignarEmpleado() {
        // 1. Tarea PENDIENTE sin empleado asignado
        TareaHousekeeping tarea = crearTareaDePrueba(1L, EstadoTarea.PENDIENTE);
        tarea.setEmpleadoAsignado(null);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));

        TareaHousekeeping conEmpleado = crearTareaDePrueba(1L, EstadoTarea.EN_PROCESO);
        conEmpleado.setEmpleadoAsignado("Carlos Pérez");
        when(tareaRepository.save(any(TareaHousekeeping.class))).thenReturn(conEmpleado);

        // 2. Llamar al servicio
        TareaHousekeeping resultado = tareaService.asignarEmpleado(1L, "Carlos Pérez");

        // 3. Verificar empleado asignado y cambio de estado
        assertNotNull(resultado);
        assertEquals("Carlos Pérez", resultado.getEmpleadoAsignado());
        assertEquals(EstadoTarea.EN_PROCESO, resultado.getEstado());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 9: eliminar() → debe llamar a repository.delete() una vez
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testEliminar() {
        // 1. La tarea existe
        TareaHousekeeping tarea = crearTareaDePrueba(1L, EstadoTarea.PENDIENTE);
        when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
        doNothing().when(tareaRepository).delete(tarea);

        // 2. Llamar al servicio
        tareaService.eliminar(1L);

        // 3. Verificar que delete() fue llamado exactamente una vez
        verify(tareaRepository, times(1)).delete(tarea);
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 10: obtenerPorHabitacion() → debe retornar tareas de esa habitación
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorHabitacion() {
        // 1. Repositorio devuelve tareas de la habitación 10
        TareaHousekeeping tarea = crearTareaDePrueba(1L, EstadoTarea.PENDIENTE);
        when(tareaRepository.findByHabitacionId(10L)).thenReturn(List.of(tarea));

        // 2. Llamar al servicio
        List<TareaHousekeeping> resultado = tareaService.obtenerPorHabitacion(10L);

        // 3. Verificar resultado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getHabitacionId());
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
