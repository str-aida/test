CREATE TABLE descuento (
    id_descuento BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    id_establecimiento BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    CONSTRAINT fk_descuento_establecimiento FOREIGN KEY (id_establecimiento) REFERENCES establecimiento(id_establecimiento),
    CONSTRAINT chk_fechas CHECK (fecha_fin >= fecha_inicio)
);

CREATE TABLE descuento_producto (
    id_descuento_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_descuento BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    porcentaje DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_dp_descuento FOREIGN KEY (id_descuento) REFERENCES descuento(id_descuento),
    CONSTRAINT fk_dp_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT uq_descuento_producto UNIQUE (id_descuento, id_producto),
    CONSTRAINT chk_porcentaje CHECK (porcentaje > 0 AND porcentaje <= 100)
);

CREATE INDEX idx_descuento_vigencia ON descuento (id_establecimiento, estado, fecha_inicio, fecha_fin);