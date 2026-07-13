package com.hotel.msreserva;

import com.hotel.msreserva.dto.ClienteDTO;
import com.hotel.msreserva.dto.HabitacionDTO;
import com.hotel.msreserva.dto.ReservaRequest;
import com.hotel.msreserva.model.Reserva;
import com.hotel.msreserva.model.EstadoReserva;
import com.hotel.msreserva.repository.ReservaRepository;
import com.hotel.msreserva.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MsReservaApplicationTests {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ReservaService reservaService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simulación fluida usando las interfaces exactas de Spring
        when(webClientBuilder.build()).thenReturn(webClient);

        // Métodos HTTP principales
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(webClient.put()).thenReturn(requestBodyUriSpec);

        // Rutas .uri(...) configuradas de manera genérica para evitar ClassCastException
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);

        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodySpec);

        // Cuerpos y respuestas (.bodyValue y .retrieve)
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void debeCrearReservaExitosamente() {
        // 1. GIVEN
        ReservaRequest request = new ReservaRequest();
        request.setClienteId(1L);
        request.setHabitacionId(102L);
        request.setFechaEntrada(LocalDate.now());
        request.setFechaSalida(LocalDate.now().plusDays(2));
        request.setTotal(150000.0);

        ClienteDTO clienteMock = new ClienteDTO();
        HabitacionDTO habitacionMock = new HabitacionDTO();

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(55L);
        reservaGuardada.setClienteId(1L);
        reservaGuardada.setEstado(EstadoReserva.PENDIENTE);

        // Respuestas de los microservicios externos
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.just(clienteMock));
        when(responseSpec.bodyToMono(HabitacionDTO.class)).thenReturn(Mono.just(habitacionMock));
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        when(reservaRepository.save(any())).thenReturn(reservaGuardada);

        // 2. WHEN
        Reserva resultado = reservaService.crearReserva(request);

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(55L, resultado.getId());
        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void debeActualizarEstadoDeReservaExitosamente() {
        // 1. GIVEN
        Long reservaId = 1L;
        Reserva reservaMock = new Reserva();
        reservaMock.setId(reservaId);
        reservaMock.setEstado(EstadoReserva.PENDIENTE);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN
        Reserva resultado = reservaService.actualizarEstado(reservaId, EstadoReserva.CONFIRMADA);

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(EstadoReserva.CONFIRMADA, resultado.getEstado());
    }

    @Test
    void debeLanzarExcepcionCuandoReservaNoExisteAlActualizar() {
        // 1. GIVEN
        Long idInvalido = 999L;
        when(reservaRepository.findById(idInvalido)).thenReturn(Optional.empty());

        // 2. WHEN & THEN
        Exception excepcion = assertThrows(RuntimeException.class, () -> {
            reservaService.actualizarEstado(idInvalido, EstadoReserva.CONFIRMADA);
        });

        assertEquals("Reserva no encontrada", excepcion.getMessage());
    }

    @Test
    void debeObtenerReservaPorIdExitosamente() {
        // 1. GIVEN
        Long reservaId = 10L;
        Reserva reservaMock = new Reserva();
        reservaMock.setId(reservaId);

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reservaMock));

        // 2. WHEN
        Optional<Reserva> resultado = reservaService.obtenerReserva(reservaId);

        // 3. THEN
        assertTrue(resultado.isPresent());
        assertEquals(reservaId, resultado.get().getId());
    }
}