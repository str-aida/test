ALTER TABLE cupon_usuario
    ADD COLUMN reservado TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN pedido_reserva_id BIGINT NULL;
