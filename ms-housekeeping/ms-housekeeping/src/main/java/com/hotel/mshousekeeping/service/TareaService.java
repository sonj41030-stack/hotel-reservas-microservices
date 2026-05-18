package com.hotel.mshousekeeping.service;

import com.hotel.mshousekeeping.client.HabitacionClient;
import com.hotel.mshousekeeping.dto.TareaDTO;
import com.hotel.mshousekeeping.model.EstadoTarea;
import com.hotel.mshousekeeping.model.TareaHousekeeping;
import com.hotel.mshousekeeping.repository.TareaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TareaService {

    private static final Logger log = LoggerFactory.getLogger(TareaService.class);

    private final TareaRepository tareaRepository;
    private final HabitacionClient habitacionClient;

    public TareaService(TareaRepository tareaRepository,
                        HabitacionClient habitacionClient) {
        this.tareaRepository = tareaRepository;
        this.habitacionClient = habitacionClient;
    }

    // ── LISTAR TODAS ──────────────────────────────────────────────────────────
    public List<TareaHousekeeping> listarTodas() {
        log.info("[HOUSEKEEPING] Listando todas las tareas");
        return tareaRepository.findAll();
    }

    // ── OBTENER POR ID ────────────────────────────────────────────────────────
    public TareaHousekeeping obtenerPorId(Long id) {
        log.info("[HOUSEKEEPING] Buscando tarea con id: {}", id);
        return tareaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[HOUSEKEEPING] Tarea no encontrada con id: {}", id);
                    return new RuntimeException("Tarea no encontrada con id: " + id);
                });
    }

    // ── CREAR ─────────────────────────────────────────────────────────────────
    public TareaHousekeeping crear(TareaDTO dto) {
        log.info("[HOUSEKEEPING] Creando tarea tipo '{}' para habitación {}",
                dto.getTipoTarea(), dto.getHabitacionId());

        // Regla de negocio: validar que la habitación existe en ms-habitaciones
        try {
            habitacionClient.obtenerHabitacion(dto.getHabitacionId());
            log.info("[HOUSEKEEPING] Habitación {} validada correctamente", dto.getHabitacionId());
        } catch (Exception e) {
            log.error("[HOUSEKEEPING] Habitación {} no encontrada en ms-habitaciones",
                    dto.getHabitacionId());
            throw new RuntimeException(
                    "No se puede crear la tarea: habitación con id "
                            + dto.getHabitacionId() + " no existe en el sistema"
            );
        }

        TareaHousekeeping tarea = new TareaHousekeeping();
        tarea.setHabitacionId(dto.getHabitacionId());
        tarea.setTipoTarea(dto.getTipoTarea().toUpperCase());
        tarea.setEmpleadoAsignado(dto.getEmpleadoAsignado());
        tarea.setPrioridad(
                dto.getPrioridad() != null ? dto.getPrioridad().toUpperCase() : "MEDIA"
        );
        tarea.setObservaciones(dto.getObservaciones());
        tarea.setEstado(EstadoTarea.PENDIENTE);

        TareaHousekeeping guardada = tareaRepository.save(tarea);
        log.info("[HOUSEKEEPING] Tarea creada con id: {}", guardada.getId());
        return guardada;
    }

    // ── CAMBIAR ESTADO ────────────────────────────────────────────────────────
    public TareaHousekeeping cambiarEstado(Long id, String nuevoEstado) {
        log.info("[HOUSEKEEPING] Cambiando estado de tarea {} a '{}'", id, nuevoEstado);
        TareaHousekeeping tarea = obtenerPorId(id);

        // Regla de negocio: no se puede cambiar estado de una tarea ya COMPLETADA
        if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
            log.warn("[HOUSEKEEPING] Intento de modificar tarea COMPLETADA: {}", id);
            throw new RuntimeException(
                    "No se puede cambiar el estado: la tarea " + id + " ya está COMPLETADA"
            );
        }

        // Regla de negocio: no se puede cambiar estado de una tarea CANCELADA
        if (tarea.getEstado() == EstadoTarea.CANCELADA) {
            log.warn("[HOUSEKEEPING] Intento de modificar tarea CANCELADA: {}", id);
            throw new RuntimeException(
                    "No se puede cambiar el estado: la tarea " + id + " está CANCELADA"
            );
        }

        try {
            EstadoTarea estado = EstadoTarea.valueOf(nuevoEstado.toUpperCase());
            tarea.setEstado(estado);

            // Si se completa, registrar la fecha
            if (estado == EstadoTarea.COMPLETADA) {
                tarea.setFechaCompletado(LocalDateTime.now());
                log.info("[HOUSEKEEPING] Tarea {} marcada como COMPLETADA", id);
            }

            return tareaRepository.save(tarea);

        } catch (IllegalArgumentException e) {
            log.error("[HOUSEKEEPING] Estado inválido: '{}'", nuevoEstado);
            throw new RuntimeException(
                    "Estado inválido: '" + nuevoEstado
                            + "'. Valores válidos: PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA"
            );
        }
    }

    // ── ASIGNAR EMPLEADO ──────────────────────────────────────────────────────
    public TareaHousekeeping asignarEmpleado(Long id, String empleado) {
        log.info("[HOUSEKEEPING] Asignando empleado '{}' a tarea {}", empleado, id);
        TareaHousekeeping tarea = obtenerPorId(id);

        // Regla de negocio: empleado no puede estar vacío
        if (empleado == null || empleado.isBlank()) {
            throw new RuntimeException("El nombre del empleado no puede estar vacío");
        }

        tarea.setEmpleadoAsignado(empleado);
        // Al asignar empleado, pasar a EN_PROCESO automáticamente
        if (tarea.getEstado() == EstadoTarea.PENDIENTE) {
            tarea.setEstado(EstadoTarea.EN_PROCESO);
            log.info("[HOUSEKEEPING] Tarea {} pasó a EN_PROCESO al asignar empleado", id);
        }

        return tareaRepository.save(tarea);
    }

    // ── FILTROS ───────────────────────────────────────────────────────────────
    public List<TareaHousekeeping> obtenerPorHabitacion(Long habitacionId) {
        log.info("[HOUSEKEEPING] Buscando tareas de habitación: {}", habitacionId);
        return tareaRepository.findByHabitacionId(habitacionId);
    }

    public List<TareaHousekeeping> obtenerPorEstado(String estado) {
        log.info("[HOUSEKEEPING] Buscando tareas con estado: {}", estado);
        try {
            return tareaRepository.findByEstado(
                    EstadoTarea.valueOf(estado.toUpperCase())
            );
        } catch (IllegalArgumentException e) {
            log.error("[HOUSEKEEPING] Estado inválido en filtro: {}", estado);
            throw new RuntimeException("Estado inválido: " + estado);
        }
    }

    public List<TareaHousekeeping> obtenerPorEmpleado(String empleado) {
        log.info("[HOUSEKEEPING] Buscando tareas del empleado: {}", empleado);
        return tareaRepository.findByEmpleadoAsignado(empleado);
    }

    public List<TareaHousekeeping> obtenerPorPrioridad(String prioridad) {
        log.info("[HOUSEKEEPING] Buscando tareas con prioridad: {}", prioridad);
        return tareaRepository.findByPrioridad(prioridad.toUpperCase());
    }

    // ── ELIMINAR ──────────────────────────────────────────────────────────────
    public void eliminar(Long id) {
        log.info("[HOUSEKEEPING] Eliminando tarea con id: {}", id);
        TareaHousekeeping tarea = obtenerPorId(id);
        tareaRepository.delete(tarea);
        log.info("[HOUSEKEEPING] Tarea {} eliminada correctamente", id);
    }
}