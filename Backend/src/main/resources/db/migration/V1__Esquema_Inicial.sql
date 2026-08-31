-- Migración V1: esquema inicial completo
-- Incluye todas las tablas en el orden correcto de dependencias.
-- NOTA: `establecimiento` incluye la columna `dias_habiles` porque V2 la elimina
-- y crea la tabla `establecimiento_dias_habiles` en su reemplazo.

-- --------------------------------------------------------
-- Tabla: establecimiento
-- --------------------------------------------------------
CREATE TABLE establecimiento (
  id_establecimiento BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(200) NOT NULL,
  razon_social VARCHAR(300) NOT NULL,
  cuit VARCHAR(20) NOT NULL,
  email VARCHAR(150) DEFAULT NULL,
  telefono VARCHAR(20) NOT NULL,
  horario_apertura TIME NOT NULL,
  horario_cierre TIME NOT NULL,
  tipo_servicio ENUM('DELIVERY','RETIRO','AMBOS') NOT NULL,
  descripcion VARCHAR(300) DEFAULT NULL,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  id_direccion BIGINT DEFAULT NULL,
  dias_habiles VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id_establecimiento),
  UNIQUE KEY cuit (cuit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: usuario
-- --------------------------------------------------------
CREATE TABLE usuario (
  id_usuario BIGINT NOT NULL AUTO_INCREMENT,
  id_establecimiento BIGINT DEFAULT NULL,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  password VARCHAR(255) NOT NULL,
  telefono VARCHAR(20) NOT NULL,
  dni VARCHAR(20) NOT NULL,
  fecha_nacimiento DATE NOT NULL,
  rol ENUM('ADMIN','EMPLEADO','CLIENTE') NOT NULL,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY email (email),
  UNIQUE KEY dni (dni),
  CONSTRAINT usuario_ibfk_1 FOREIGN KEY (id_establecimiento) REFERENCES establecimiento (id_establecimiento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: direccion
-- --------------------------------------------------------
CREATE TABLE direccion (
  id_direccion BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario BIGINT DEFAULT NULL,
  nombre VARCHAR(50) DEFAULT NULL,
  calle VARCHAR(100) NOT NULL,
  numero VARCHAR(20) NOT NULL,
  localidad VARCHAR(100) NOT NULL,
  piso VARCHAR(20) DEFAULT NULL,
  departamento VARCHAR(20) DEFAULT NULL,
  codigo_postal VARCHAR(10) DEFAULT NULL,
  referencia VARCHAR(200) DEFAULT NULL,
  es_principal TINYINT(1) NOT NULL DEFAULT 0,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
  PRIMARY KEY (id_direccion),
  CONSTRAINT direccion_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Ahora sí, la FK circular establecimiento -> direccion
ALTER TABLE establecimiento
  ADD CONSTRAINT establecimiento_ibfk_direccion FOREIGN KEY (id_direccion) REFERENCES direccion (id_direccion);

-- --------------------------------------------------------
-- Tabla: categoria
-- --------------------------------------------------------
CREATE TABLE categoria (
  id_categoria BIGINT NOT NULL AUTO_INCREMENT,
  id_establecimiento BIGINT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(200) DEFAULT NULL,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_categoria),
  UNIQUE KEY uk_categoria_nombre (id_establecimiento, nombre),
  CONSTRAINT categoria_ibfk_1 FOREIGN KEY (id_establecimiento) REFERENCES establecimiento (id_establecimiento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: cupon
-- --------------------------------------------------------
CREATE TABLE cupon (
  id_cupon BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(50) NOT NULL,
  tipo_descuento ENUM('PORCENTAJE','MONTO') NOT NULL,
  valor DECIMAL(10,2) NOT NULL,
  fecha_inicio DATE NOT NULL,
  fecha_fin DATE NOT NULL,
  uso_maximo INT DEFAULT NULL,
  usos_actuales INT DEFAULT 0,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_cupon),
  UNIQUE KEY codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: producto
-- --------------------------------------------------------
CREATE TABLE producto (
  id_producto BIGINT NOT NULL AUTO_INCREMENT,
  id_establecimiento BIGINT NOT NULL,
  id_categoria BIGINT NOT NULL,
  nombre VARCHAR(150) NOT NULL,
  descripcion VARCHAR(500) DEFAULT NULL,
  precio DECIMAL(10,2) NOT NULL,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  stock INT NOT NULL DEFAULT 0,
  imagen_url VARCHAR(300) DEFAULT NULL,
  codigo VARCHAR(50) DEFAULT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id_producto),
  UNIQUE KEY codigo (codigo),
  CONSTRAINT producto_ibfk_1 FOREIGN KEY (id_establecimiento) REFERENCES establecimiento (id_establecimiento),
  CONSTRAINT producto_ibfk_2 FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: cupon_usuario
-- --------------------------------------------------------
CREATE TABLE cupon_usuario (
  id_cupon_usuario BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario BIGINT NOT NULL,
  id_cupon BIGINT NOT NULL,
  usado TINYINT(1) DEFAULT 0,
  fecha_asignacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_uso DATETIME DEFAULT NULL,
  PRIMARY KEY (id_cupon_usuario),
  CONSTRAINT cupon_usuario_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CONSTRAINT cupon_usuario_ibfk_2 FOREIGN KEY (id_cupon) REFERENCES cupon (id_cupon)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: pedido
-- --------------------------------------------------------
CREATE TABLE pedido (
  id_pedido BIGINT NOT NULL AUTO_INCREMENT,
  id_establecimiento BIGINT NOT NULL,
  id_usuario BIGINT NOT NULL,
  fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
  estado ENUM('PENDIENTE','ACEPTADO','EN_PREPARACION','LISTO','ENTREGADO','RECHAZADO') NOT NULL,
  tipo_entrega ENUM('DELIVERY','RETIRO') NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  nombre_cliente VARCHAR(150) NOT NULL,
  telefono_cliente VARCHAR(20) NOT NULL,
  direccion_cliente VARCHAR(300) DEFAULT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  numero_pedido VARCHAR(20) DEFAULT NULL,
  id_cupon BIGINT DEFAULT NULL,
  PRIMARY KEY (id_pedido),
  CONSTRAINT fk_pedido_cupon FOREIGN KEY (id_cupon) REFERENCES cupon (id_cupon),
  CONSTRAINT pedido_ibfk_1 FOREIGN KEY (id_establecimiento) REFERENCES establecimiento (id_establecimiento),
  CONSTRAINT pedido_ibfk_2 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CONSTRAINT chk_direccion_delivery CHECK (((tipo_entrega = 'DELIVERY' AND direccion_cliente IS NOT NULL) OR tipo_entrega = 'RETIRO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: detalle_pedido
-- --------------------------------------------------------
CREATE TABLE detalle_pedido (
  id_detalle BIGINT NOT NULL AUTO_INCREMENT,
  id_pedido BIGINT NOT NULL,
  id_producto BIGINT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  nombre_producto VARCHAR(150) NOT NULL,
  PRIMARY KEY (id_detalle),
  CONSTRAINT detalle_pedido_ibfk_1 FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido),
  CONSTRAINT detalle_pedido_ibfk_2 FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: pago
-- --------------------------------------------------------
CREATE TABLE pago (
  id_pago BIGINT NOT NULL AUTO_INCREMENT,
  id_pedido BIGINT NOT NULL,
  monto DECIMAL(10,2) NOT NULL,
  metodo ENUM('EFECTIVO','TARJETA','TRANSFERENCIA') NOT NULL,
  estado ENUM('PENDIENTE','APROBADO','REEMBOLSADO') NOT NULL,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion DATETIME DEFAULT NULL,
  referencia_externa VARCHAR(100) DEFAULT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  url_pago VARCHAR(500) DEFAULT NULL,
  id_transaccion_externa VARCHAR(150) DEFAULT NULL,
  PRIMARY KEY (id_pago),
  UNIQUE KEY unique_pago_pedido (id_pedido),
  CONSTRAINT pago_ibfk_1 FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: puntos_cliente
-- --------------------------------------------------------
CREATE TABLE puntos_cliente (
  id_movimiento BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario BIGINT NOT NULL,
  id_pedido BIGINT DEFAULT NULL,
  tipo ENUM('ACUMULA','CANJE') NOT NULL,
  puntos INT NOT NULL,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  descripcion VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (id_movimiento),
  CONSTRAINT puntos_cliente_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CONSTRAINT puntos_cliente_ibfk_2 FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: notificacion
-- --------------------------------------------------------
CREATE TABLE notificacion (
  id_notificacion BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario BIGINT NOT NULL,
  titulo VARCHAR(100) NOT NULL,
  mensaje VARCHAR(255) NOT NULL,
  leida TINYINT(1) NOT NULL DEFAULT 0,
  fecha DATETIME NOT NULL,
  tipo ENUM('PEDIDO','PAGO','PROMOCION','SISTEMA') NOT NULL,
  PRIMARY KEY (id_notificacion),
  CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: log_sistema
-- --------------------------------------------------------
CREATE TABLE log_sistema (
  id_log BIGINT NOT NULL AUTO_INCREMENT,
  tabla_afectada VARCHAR(50) NOT NULL,
  id_registro BIGINT NOT NULL,
  accion VARCHAR(50) NOT NULL,
  campo_modificado VARCHAR(100) DEFAULT NULL,
  valor_anterior TEXT,
  valor_nuevo TEXT,
  id_usuario BIGINT NOT NULL,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  descripcion VARCHAR(255) DEFAULT NULL,
  tipo_operacion ENUM('INSERT','UPDATE','DELETE') NOT NULL,
  referencia VARCHAR(150) DEFAULT NULL,
  PRIMARY KEY (id_log),
  CONSTRAINT log_sistema_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: recuperacion_password
-- --------------------------------------------------------
CREATE TABLE recuperacion_password (
  id_recuperacion BIGINT NOT NULL AUTO_INCREMENT,
  token VARCHAR(255) NOT NULL,
  id_usuario BIGINT NOT NULL,
  fecha_expiracion DATETIME NOT NULL,
  usado TINYINT(1) NOT NULL DEFAULT 0,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_recuperacion),
  UNIQUE KEY token (token),
  CONSTRAINT fk_recuperacion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;