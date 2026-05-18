package com.hotel.mshousekeeping.repository;

import com.hotel.mshousekeeping.model.EstadoTarea;
import com.hotel.mshousekeeping.model.TareaHousekeeping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaHousekeeping, Long> {

    // Buscar todas las tareas de una habitación
    List<TareaHousekeeping> findByHabitacionId(Long habitacionId);

    // Buscar por estado (PENDIENTE, EN_PROCESO, etc.)
    List<TareaHousekeeping> findByEstado(EstadoTarea estado);

    // Buscar por empleado asignado
    List<TareaHousekeeping> findByEmpleadoAsignado(String empleadoAsignado);

    // Buscar por prioridad
    List<TareaHousekeeping> findByPrioridad(String prioridad);
}