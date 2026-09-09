ALTER TABLE establecimiento
ADD COLUMN logo_url VARCHAR(500) NULL;

UPDATE establecimiento
SET logo_url = ''
WHERE logo_url IS NULL;

ALTER TABLE establecimiento
MODIFY COLUMN logo_url VARCHAR(500) NOT NULL;