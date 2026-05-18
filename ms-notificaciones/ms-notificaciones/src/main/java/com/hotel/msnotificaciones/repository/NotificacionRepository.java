package com.hotel.msnotificaciones.repository;

import com.hotel.msnotificaciones.model.EstadoNotificacion;
import com.hotel.msnotificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Buscar todas las notificaciones de un cliente
    List<Notificacion> findByClienteId(Long clienteId);

    // Buscar por estado (PENDIENTE, ENVIADA, FALLIDA)
    List<Notificacion> findByEstado(EstadoNotificacion estado);

    // Buscar por reserva
    List<Notificacion> findByReservaId(Long reservaId);

    // Buscar por tipo (CONFIRMACION_RESERVA, RECORDATORIO, etc.)
    List<Notificacion> findByTipo(String tipo);
}