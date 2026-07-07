package com.hotel.mspagos.dto;

import com.hotel.mspagos.model.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequest {

    @NotNull
    private Long reservaId;

    @NotNull
    private  Double monto;

    @NotNull
    private MetodoPago metodoPago;

}
