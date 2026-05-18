package com.hotel.msreserva.repository;

import com.hotel.msreserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByHabitacionId(Long habitacionId);
    List<Reserva> findByEstado(com.hotel.msreserva.model.EstadoReserva estado);
}
