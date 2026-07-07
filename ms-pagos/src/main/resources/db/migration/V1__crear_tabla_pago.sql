CREATE TABLE IF NOT EXISTS pagos (
             id BIGINT PRIMARY KEY AUTO_INCREMENT,
             reserva_id BIGINT NOT NULL,
             monto DOUBLE NOT NULL,
             estado VARCHAR(50),
            metodo_pago VARCHAR(50),
            fecha_pago DATETIME
    );
