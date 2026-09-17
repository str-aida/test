CREATE TABLE regla_cupon (
    id_regla BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_asignacion VARCHAR(30) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_descuento VARCHAR(20) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    dias_validez INT NOT NULL DEFAULT 30,
    cantidad_compras_requeridas INT NULL,
    descripcion VARCHAR(255) NULL
);

INSERT INTO regla_cupon (tipo_asignacion, activo, tipo_descuento, valor, dias_validez, cantidad_compras_requeridas, descripcion)
VALUES 
('CUMPLEANOS', TRUE, 'PORCENTAJE', 15.00, 30, NULL, 'Cupón de regalo por cumpleaños'),
('CANTIDAD_COMPRAS', TRUE, 'PORCENTAJE', 10.00, 30, 3, 'Cupón de fidelidad cada N compras entregadas');
