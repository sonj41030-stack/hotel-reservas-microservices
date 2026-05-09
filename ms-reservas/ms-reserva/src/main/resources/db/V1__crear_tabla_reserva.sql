CREATE TABLE hotel_reservas (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         cliente_id BIGINT NOT NULL,
                         habitacion_id BIGINT NOT NULL,
                         fecha_entrada DATE NOT NULL,
                         fecha_salida DATE NOT NULL,
                         total DECIMAL(10,2) NOT NULL,
                         estado VARCHAR(50) NOT NULL
);