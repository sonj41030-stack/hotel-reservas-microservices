package com.hotel.mspagos.dto;

import com.hotel.mspagos.model.EstadoPago;
import com.hotel.mspagos.model.MetodoPago;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoResponse {

    private Long id;
    private Long reservaId;
    private Double monto;
    private EstadoPago estado;
    private MetodoPago metodoPago;
    private LocalDateTime fechaPago;

}
