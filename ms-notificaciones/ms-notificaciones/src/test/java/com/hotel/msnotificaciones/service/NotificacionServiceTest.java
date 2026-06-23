package com.hotel.msnotificaciones.service;

import com.hotel.msnotificaciones.client.ClienteClient;
import com.hotel.msnotificaciones.dto.NotificacionRequestDTO;
import com.hotel.msnotificaciones.dto.NotificacionResponseDTO;
import com.hotel.msnotificaciones.model.EstadoNotificacion;
import com.hotel.msnotificaciones.model.Notificacion;
import com.hotel.msnotificaciones.repository.NotificacionRepository;
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

/**
 * Tests a nivel de SERVICIO para NotificacionService.
 *
 * Se usa @ExtendWith(MockitoExtension.class) en vez de @SpringBootTest
 * para NO levantar el contexto de Spring (evita problemas con Eureka/Feign).
 *
 * @Mock crea mocks puros de Mockito.
 * @InjectMocks crea la instancia real del servicio e inyecta los mocks.
 *
 * Patrón de cada test:
 *   1. Configurar mock (when...thenReturn)
 *   2. Llamar al método del servicio
 *   3. Verificar resultado (assert...)
 */
@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    // @InjectMocks → crea el servicio REAL e inyecta los @Mock automáticamente
    @InjectMocks
    private NotificacionService notificacionService;

    // @Mock → simula el repositorio sin conectarse a la BD
    @Mock
    private NotificacionRepository notificacionRepository;

    // @Mock → simula la llamada Feign a ms-clientes
    @Mock
    private ClienteClient clienteClient;

    // ─────────────────────────────────────────────────────────────────
    // TEST 1: listarTodas() → debe retornar lista no vacía
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testListarTodas() {
        // 1. Configurar mock: cuando se llame findAll(), devolver una notificación de prueba
        Notificacion notificacion = crearNotificacionDePrueba(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));

        // 2. Llamar al método del servicio
        List<NotificacionResponseDTO> resultado = notificacionService.listarTodas();

        // 3. Verificar que la lista no sea nula y contenga exactamente un elemento
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 2: obtenerPorId() → debe retornar el DTO correcto
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorId() {
        // 1. Configurar mock: cuando busque id=1, devolver la notificación
        Notificacion notificacion = crearNotificacionDePrueba(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        // 2. Llamar al servicio
        NotificacionResponseDTO resultado = notificacionService.obtenerPorId(1L);

        // 3. Verificar que el DTO tenga los datos correctos
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("CONFIRMACION_RESERVA", resultado.getTipo());
        assertEquals(EstadoNotificacion.PENDIENTE, resultado.getEstado());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 3: obtenerPorId() con id inexistente → debe lanzar excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorIdNoExiste() {
        // 1. Cuando busque id=99, no encontrar nada
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // 2 y 3. Verificar que se lanza RuntimeException con el id en el mensaje
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 4: crear() → debe guardar y retornar el DTO con estado PENDIENTE
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCrear() {
        // 1. Preparar DTO de entrada y configurar mocks
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setClienteId(10L);
        request.setReservaId(5L);
        request.setTipo("CONFIRMACION_RESERVA");
        request.setMensaje("Tu reserva fue confirmada");
        request.setEmailDestino("cliente@test.com");

        // clienteClient no lanza excepción → cliente existe
        when(clienteClient.verificarCliente(10L)).thenReturn(null);

        // El repositorio devuelve la notificación ya guardada
        Notificacion guardada = crearNotificacionDePrueba(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(guardada);

        // 2. Llamar al servicio
        NotificacionResponseDTO resultado = notificacionService.crear(request);

        // 3. Verificar resultado
        assertNotNull(resultado);
        assertEquals(EstadoNotificacion.PENDIENTE, resultado.getEstado());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 5: crear() con tipo inválido → debe lanzar excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testCrearTipoInvalido() {
        // 1. DTO con tipo que no está en la lista permitida
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setClienteId(10L);
        request.setTipo("TIPO_INVENTADO");
        request.setMensaje("Mensaje");
        request.setEmailDestino("test@test.com");

        when(clienteClient.verificarCliente(10L)).thenReturn(null);

        // 2 y 3. El servicio debe rechazarlo
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.crear(request));

        assertTrue(ex.getMessage().contains("Tipo inválido"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 6: marcarComoEnviada() desde PENDIENTE → debe cambiar a ENVIADA
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testMarcarComoEnviada() {
        // 1. Notificación en estado PENDIENTE
        Notificacion notificacion = crearNotificacionDePrueba(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        Notificacion enviada = crearNotificacionDePrueba(1L, EstadoNotificacion.ENVIADA);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(enviada);

        // 2. Llamar al servicio
        NotificacionResponseDTO resultado = notificacionService.marcarComoEnviada(1L);

        // 3. El estado debe ser ENVIADA
        assertEquals(EstadoNotificacion.ENVIADA, resultado.getEstado());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 7: marcarComoEnviada() si ya está ENVIADA → lanza excepción
    //         (regla de negocio: solo se envía si está PENDIENTE)
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testMarcarComoEnviadaYaEnviada() {
        // 1. Notificación ya ENVIADA
        Notificacion notificacion = crearNotificacionDePrueba(1L, EstadoNotificacion.ENVIADA);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        // 2 y 3. Debe lanzar excepción por regla de negocio
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.marcarComoEnviada(1L));

        assertTrue(ex.getMessage().contains("PENDIENTE"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 8: marcarComoFallida() desde ENVIADA → lanza excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testMarcarComoFallidaDesdeEnviada() {
        // 1. No se puede fallar algo ya enviado
        Notificacion notificacion = crearNotificacionDePrueba(1L, EstadoNotificacion.ENVIADA);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        // 2 y 3.
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.marcarComoFallida(1L));

        assertTrue(ex.getMessage().contains("fallida"));
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 9: reenviar() desde FALLIDA → debe volver a PENDIENTE
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testReenviar() {
        // 1. Notificación en estado FALLIDA
        Notificacion notificacion = crearNotificacionDePrueba(1L, EstadoNotificacion.FALLIDA);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));

        Notificacion pendiente = crearNotificacionDePrueba(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(pendiente);

        // 2. Llamar al servicio
        NotificacionResponseDTO resultado = notificacionService.reenviar(1L);

        // 3. El estado debe volver a PENDIENTE
        assertEquals(EstadoNotificacion.PENDIENTE, resultado.getEstado());
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 10: eliminar() → debe llamar a repository.delete() una sola vez
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testEliminar() {
        // 1. La notificación existe
        Notificacion notificacion = crearNotificacionDePrueba(1L, EstadoNotificacion.PENDIENTE);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        doNothing().when(notificacionRepository).delete(notificacion);

        // 2. Llamar al servicio
        notificacionService.eliminar(1L);

        // 3. Verificar que delete() fue llamado exactamente una vez
        verify(notificacionRepository, times(1)).delete(notificacion);
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST 11: obtenerPorEstado() con estado inválido → lanza excepción
    // ─────────────────────────────────────────────────────────────────
    @Test
    void testObtenerPorEstadoInvalido() {
        // Estado que no existe en el enum EstadoNotificacion
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.obtenerPorEstado("ESTADO_RARO"));

        assertTrue(ex.getMessage().contains("Estado inválido"));
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper: crea una Notificacion de prueba reutilizable
    // ─────────────────────────────────────────────────────────────────
    private Notificacion crearNotificacionDePrueba(Long id, EstadoNotificacion estado) {
        Notificacion n = new Notificacion();
        n.setId(id);
        n.setClienteId(10L);
        n.setReservaId(5L);
        n.setTipo("CONFIRMACION_RESERVA");
        n.setMensaje("Reserva confirmada");
        n.setEmailDestino("cliente@hotel.com");
        n.setEstado(estado);
        return n;
    }
}