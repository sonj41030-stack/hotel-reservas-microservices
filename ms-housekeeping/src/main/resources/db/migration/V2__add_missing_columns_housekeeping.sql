ALTER TABLE tareas_housekeeping
    ADD COLUMN tipo_tarea VARCHAR(50) NOT NULL DEFAULT 'LIMPIEZA',
    ADD COLUMN empleado_asignado VARCHAR(100),
    ADD COLUMN prioridad VARCHAR(20) DEFAULT 'MEDIA',
    ADD COLUMN observaciones TEXT;