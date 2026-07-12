package com.hotel.msnotificaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID externo del cliente (viene de ms-clientes)
    @NotNull(message = "El clienteId es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    // ID externo de la reserva (viene de ms-reservas), puede ser null
    @Column(name = "reserva_id")
    private Long reservaId;

    // Tipo: CONFIRMACION_RESERVA, RECORDATORIO, ALERTA_SISTEMA, CANCELACION
    @NotBlank(message = "El tipo es obligatorio")
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @NotBlank(message = "El email destino es obligatorio")
    @Column(name = "email_destino", nullable = false, length = 150)
    private String emailDestino;

    // ✅ @JdbcTypeCode(SqlTypes.VARCHAR) le dice a Hibernate que trate el enum
    // como VARCHAR en vez de ENUM nativo de MySQL (que es el comportamiento
    // por defecto de Hibernate 6 con Java 21)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    // Se ejecuta automáticamente antes de insertar en BD
    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoNotificacion.PENDIENTE;
        }
    }
}