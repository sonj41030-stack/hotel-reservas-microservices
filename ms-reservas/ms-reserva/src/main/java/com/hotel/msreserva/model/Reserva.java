package com.hotel.msreserva.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id") // <--- Esto mapea clienteId a cliente_id en SQL
    private Long clienteId;

    @Column(name = "habitacion_id") // <--- Esto mapea habitacionId a habitacion_id
    private Long habitacionId;

    @Column(name = "fecha_entrada") // <--- Mismo caso para la fecha
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    private Double total;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;
}