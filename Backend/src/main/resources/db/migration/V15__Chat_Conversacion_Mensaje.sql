CREATE TABLE chat_conversacion (
    id_conversacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_establecimiento BIGINT NOT NULL,
    id_pedido BIGINT NOT NULL,
    id_cliente BIGINT NOT NULL,
    id_agente BIGINT NULL,
    estado VARCHAR(20) NOT NULL,
    cerrado_por VARCHAR(20) NULL,
    fecha_creacion DATETIME NOT NULL,
    fecha_actualizacion DATETIME NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_chat_establecimiento FOREIGN KEY (id_establecimiento) REFERENCES establecimiento(id_establecimiento),
    CONSTRAINT fk_chat_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    CONSTRAINT fk_chat_cliente FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_chat_agente FOREIGN KEY (id_agente) REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_chat_pedido_estado ON chat_conversacion (id_pedido, estado);
CREATE INDEX idx_chat_establecimiento_estado ON chat_conversacion (id_establecimiento, estado);
CREATE INDEX idx_chat_cliente ON chat_conversacion (id_cliente);

CREATE TABLE chat_mensaje (
    id_mensaje BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_conversacion BIGINT NOT NULL,
    id_remitente BIGINT NOT NULL,
    es_sistema BOOLEAN NOT NULL DEFAULT FALSE,
    contenido TEXT NOT NULL,
    fecha_envio DATETIME NOT NULL,
    leido BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_mensaje_conversacion FOREIGN KEY (id_conversacion) REFERENCES chat_conversacion(id_conversacion),
    CONSTRAINT fk_mensaje_remitente FOREIGN KEY (id_remitente) REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_mensaje_conversacion_fecha ON chat_mensaje (id_conversacion, fecha_envio);
CREATE INDEX idx_mensaje_leido ON chat_mensaje (id_conversacion, leido);