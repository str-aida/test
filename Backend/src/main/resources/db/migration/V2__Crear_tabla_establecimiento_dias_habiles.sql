CREATE TABLE establecimiento_dias_habiles (
    id_establecimiento BIGINT NOT NULL,
    dia VARCHAR(20) NOT NULL,

    PRIMARY KEY (id_establecimiento, dia),

    CONSTRAINT fk_establecimiento_dias
        FOREIGN KEY (id_establecimiento)
        REFERENCES establecimiento(id_establecimiento)
        ON DELETE CASCADE
);

ALTER TABLE establecimiento
DROP COLUMN dias_habiles;