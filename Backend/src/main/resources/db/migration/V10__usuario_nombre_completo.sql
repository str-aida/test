CREATE INDEX idx_usuario_nombre_completo ON usuario ((LOWER(CONCAT(nombre, ' ', apellido))));
