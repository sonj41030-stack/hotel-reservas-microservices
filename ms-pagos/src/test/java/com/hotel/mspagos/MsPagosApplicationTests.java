package com.hotel.mspagos;

import com.hotel.mspagos.dto.PagoRequest;
import com.hotel.mspagos.dto.PagoResponse;
import com.hotel.mspagos.model.EstadoPago;
import com.hotel.mspagos.model.MetodoPago;
import com.hotel.mspagos.model.Pago;
import com.hotel.mspagos.repository.PagoRepository;
import com.hotel.mspagos.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MsPagosApplicationTests {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private PagoService pagoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void debeProcesarPagoExitosamente() {
        // 1. GIVEN (Preparación de datos)
        PagoRequest request = new PagoRequest();
        request.setReservaId(10L);
        request.setMonto(50000.0);

        // CORREGIDO: Tomamos dinámicamente el primer valor disponible en tu Enum MetodoPago
        MetodoPago metodoTest = MetodoPago.values()[0];
        request.setMetodoPago(metodoTest);

        Pago pagoGuardado = new Pago();
        pagoGuardado.setId(1L);
        pagoGuardado.setReservaId(10L);
        pagoGuardado.setMonto(50000.0);
        pagoGuardado.setMetodoPago(metodoTest);
        pagoGuardado.setEstado(EstadoPago.COMPLETADO);

        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);

        // 2. WHEN (Ejecución de la lógica)
        PagoResponse response = pagoService.procesarPago(request);

        // 3. THEN (Verificación de resultados)
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(EstadoPago.COMPLETADO, response.getEstado());
        assertEquals(50000.0, response.getMonto());
    }
}