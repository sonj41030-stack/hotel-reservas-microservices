CREATE TABLE IF NOT EXISTS reportes (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        tipo VARCHAR(50) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    total_reservas INT,
    total_ingresos DOUBLE,
    porcentaje_ocupacion DOUBLE,
    hotel_id BIGINT,
    observaciones LONGTEXT
    );
