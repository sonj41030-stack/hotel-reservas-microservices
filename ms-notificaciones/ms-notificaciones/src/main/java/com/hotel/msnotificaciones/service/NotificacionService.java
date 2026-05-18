package com.hotel.msnotificaciones.service;

import com.hotel.msnotificaciones.client.ClienteClient;
import com.hotel.msnotificaciones.dto.NotificacionRequestDTO;
import com.hotel.msnotificaciones.dto.NotificacionResponseDTO;
import com.hotel.msnotificaciones.model.EstadoNotificacion;
import com.hotel.msnotificaciones.model.Notificacion;
import com.hotel.msnotificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;
    private final ClienteClient clienteClient;


    // ── Listar todas ────────────────────────────────────────────────────────────
    public List<NotificacionResponseDTO> listarTodas() {
        log.info("[NotificacionService] Listando todas las notificaciones");
        return notificacionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Obtener por ID ───────────────────────────────────────────────────────────
    public NotificacionResponseDTO obtenerPorId(Long id) {
        log.info("[NotificacionService] Buscando notificación con id: {}", id);
        Notificacion n = buscarOLanzarExcepcion(id);
        return toDTO(n);
    }

    // ── Crear ────────────────────────────────────────────────────────────────────
    public NotificacionResponseDTO crear(NotificacionRequestDTO dto) {
        log.info("[NotificacionService] Creando notificación tipo '{}' para clienteId: {}",
                dto.getTipo(), dto.getClienteId());

        // Regla de negocio: validar que el cliente existe en ms-clientes
        try {
            clienteClient.verificarCliente(dto.getClienteId());
            log.info("[NotificacionService] Cliente {} verificado correctamente", dto.getClienteId());
        } catch (Exception e) {
            log.error("[NotificacionService] Cliente {} no encontrado en ms-clientes", dto.getClienteId());
            throw new RuntimeException("El cliente con id " + dto.getClienteId() + " no existe");
        }

        // Regla de negocio: el tipo debe ser uno de los permitidos
        validarTipo(dto.getTipo());

        Notificacion n = new Notificacion();
        n.setClienteId(dto.getClienteId());
        n.setReservaId(dto.getReservaId());
        n.setTipo(dto.getTipo().toUpperCase());
        n.setMensaje(dto.getMensaje());
        n.setEmailDestino(dto.getEmailDestino());
        n.setEstado(EstadoNotificacion.PENDIENTE);

        Notificacion guardada = notificacionRepository.save(n);
        log.info("[NotificacionService] Notificación creada con id: {}", guardada.getId());
        return toDTO(guardada);
    }

    // ── Marcar como ENVIADA ──────────────────────────────────────────────────────
    public NotificacionResponseDTO marcarComoEnviada(Long id) {
        log.info("[NotificacionService] Marcando notificación {} como ENVIADA", id);
        Notificacion n = buscarOLanzarExcepcion(id);

        // Regla de negocio: solo se puede enviar si está PENDIENTE
        if (n.getEstado() != EstadoNotificacion.PENDIENTE) {
            log.warn("[NotificacionService] No se puede enviar notificación {} con estado {}",
                    id, n.getEstado());
            throw new RuntimeException(
                    "Solo se pueden enviar notificaciones en estado PENDIENTE. Estado actual: " + n.getEstado()
            );
        }

        n.setEstado(EstadoNotificacion.ENVIADA);
        n.setFechaEnvio(LocalDateTime.now());
        return toDTO(notificacionRepository.save(n));
    }

    // ── Marcar como FALLIDA ──────────────────────────────────────────────────────
    public NotificacionResponseDTO marcarComoFallida(Long id) {
        log.warn("[NotificacionService] Marcando notificación {} como FALLIDA", id);
        Notificacion n = buscarOLanzarExcepcion(id);

        // Regla de negocio: no se puede marcar fallida si ya fue enviada
        if (n.getEstado() == EstadoNotificacion.ENVIADA) {
            throw new RuntimeException("No se puede marcar como fallida una notificación ya enviada");
        }

        n.setEstado(EstadoNotificacion.FALLIDA);
        return toDTO(notificacionRepository.save(n));
    }

    // ── Reenviar (FALLIDA → PENDIENTE) ───────────────────────────────────────────
    public NotificacionResponseDTO reenviar(Long id) {
        log.info("[NotificacionService] Reenviando notificación {}", id);
        Notificacion n = buscarOLanzarExcepcion(id);

        // Regla de negocio: solo se puede reenviar si está FALLIDA
        if (n.getEstado() != EstadoNotificacion.FALLIDA) {
            throw new RuntimeException("Solo se pueden reenviar notificaciones con estado FALLIDA");
        }

        n.setEstado(EstadoNotificacion.PENDIENTE);
        n.setFechaEnvio(null);
        return toDTO(notificacionRepository.save(n));
    }

    // ── Eliminar ─────────────────────────────────────────────────────────────────
    public void eliminar(Long id) {
        log.info("[NotificacionService] Eliminando notificación {}", id);
        Notificacion n = buscarOLanzarExcepcion(id);
        notificacionRepository.delete(n);
        log.info("[NotificacionService] Notificación {} eliminada correctamente", id);
    }

    // ── Filtros ──────────────────────────────────────────────────────────────────
    public List<NotificacionResponseDTO> obtenerPorCliente(Long clienteId) {
        log.info("[NotificacionService] Buscando notificaciones del cliente {}", clienteId);
        return notificacionRepository.findByClienteId(clienteId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificacionResponseDTO> obtenerPorEstado(String estado) {
        log.info("[NotificacionService] Buscando notificaciones con estado: {}", estado);
        try {
            EstadoNotificacion e = EstadoNotificacion.valueOf(estado.toUpperCase());
            return notificacionRepository.findByEstado(e)
                    .stream().map(this::toDTO).collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            log.error("[NotificacionService] Estado inválido: {}", estado);
            throw new RuntimeException("Estado inválido: " + estado +
                    ". Valores válidos: PENDIENTE, ENVIADA, FALLIDA");
        }
    }

    public List<NotificacionResponseDTO> obtenerPorReserva(Long reservaId) {
        log.info("[NotificacionService] Buscando notificaciones de reserva {}", reservaId);
        return notificacionRepository.findByReservaId(reservaId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Helpers privados ─────────────────────────────────────────────────────────

    private Notificacion buscarOLanzarExcepcion(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[NotificacionService] Notificación no encontrada con id: {}", id);
                    return new RuntimeException("Notificación no encontrada con id: " + id);
                });
    }

    private void validarTipo(String tipo) {
        List<String> tiposValidos = List.of(
                "CONFIRMACION_RESERVA", "RECORDATORIO", "ALERTA_SISTEMA", "CANCELACION"
        );
        if (!tiposValidos.contains(tipo.toUpperCase())) {
            throw new RuntimeException("Tipo inválido: " + tipo +
                    ". Tipos válidos: " + tiposValidos);
        }
    }

    // Convierte entidad → DTO de respuesta
    private NotificacionResponseDTO toDTO(Notificacion n) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setId(n.getId());
        dto.setClienteId(n.getClienteId());
        dto.setReservaId(n.getReservaId());
        dto.setTipo(n.getTipo());
        dto.setMensaje(n.getMensaje());
        dto.setEmailDestino(n.getEmailDestino());
        dto.setEstado(n.getEstado());
        dto.setFechaCreacion(n.getFechaCreacion());
        dto.setFechaEnvio(n.getFechaEnvio());
        return dto;
    }
}