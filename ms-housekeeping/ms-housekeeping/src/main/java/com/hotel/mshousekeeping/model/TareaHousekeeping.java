package com.hotel.mshousekeeping.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas_housekeeping")
@Data
public class TareaHousekeeping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia externa a ms-habitaciones (no FK real, cada MS tiene su BD)
    @NotNull(message = "habitacionId es obligatorio")
    @Column(name = "habitacion_id", nullable = false)
    private Long habitacionId;

    @NotBlank(message = "El tipo de tarea es obligatorio")
    @Column(name = "tipo_tarea", nullable = false, length = 50)
    // Valores: LIMPIEZA, CHECK_IN, CHECK_OUT, MANTENIMIENTO
    private String tipoTarea;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    @Column(name = "empleado_asignado", length = 100)
    private String empleadoAsignado;

    @Column(name = "prioridad", length = 20)
    // BAJA, MEDIA, ALTA
    private String prioridad = "MEDIA";

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @PrePersist
    public void prePersist() {
        this.fechaAsignacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoTarea.PENDIENTE;
        }
        if (this.prioridad == null) {
            this.prioridad = "MEDIA";
        }
    }
}