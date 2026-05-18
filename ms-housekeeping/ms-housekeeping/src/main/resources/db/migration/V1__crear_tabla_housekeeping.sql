CREATE TABLE IF NOT EXISTS tareas_housekeeping (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   habitacion_id BIGINT NOT NULL,
                                                   descripcion VARCHAR(255),
    estado VARCHAR(50),
    fecha_asignacion DATETIME,
    fecha_completado DATETIME
    );