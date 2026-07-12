CREATE TABLE notificaciones (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                cliente_id BIGINT NOT NULL,
                                reserva_id BIGINT,
                                tipo VARCHAR(50) NOT NULL,
                                mensaje TEXT NOT NULL,
                                email_destino VARCHAR(150) NOT NULL,
                                estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
                                fecha_creacion DATETIME NOT NULL,
                                fecha_envio DATETIME
);

-- Índices para acelerar las búsquedas más comunes
CREATE INDEX idx_notificaciones_cliente_id ON notificaciones(cliente_id);
CREATE INDEX idx_notificaciones_reserva_id ON notificaciones(reserva_id);
CREATE INDEX idx_notificaciones_estado ON notificaciones(estado);