package com.hotel.msreserva.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long habitacionId;

    @NotNull
    private LocalDate fechaEntrada;

    @NotNull
    private LocalDate fechaSalida;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    private Double total;
}