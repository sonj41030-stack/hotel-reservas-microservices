CREATE TABLE IF NOT EXISTS reportes (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        tipo VARCHAR(50) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    total_reservas INT DEFAULT 0,
    total_ingresos DOUBLE DEFAULT 0.0,
    porcentaje_ocupacion DOUBLE DEFAULT 0.0,
    hotel_id BIGINT,
    observaciones TEXT
    );