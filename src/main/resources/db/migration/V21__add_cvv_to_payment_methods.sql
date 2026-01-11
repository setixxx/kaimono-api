ALTER TABLE payment_methods
    ADD COLUMN cvv VARCHAR(3) NOT NULL DEFAULT '000';

ALTER TABLE payment_methods
    ALTER COLUMN cvv DROP DEFAULT;

ALTER TABLE payment_methods
    ADD CONSTRAINT chk_cvv_format CHECK (length(cvv) = 3  AND cvv ~ '^[0-9]+$');

UPDATE payment_methods SET cvv = '123' WHERE id = 1;
UPDATE payment_methods SET cvv = '456' WHERE id = 2;
UPDATE payment_methods SET cvv = '789' WHERE id = 3;
UPDATE payment_methods SET cvv = '321' WHERE id = 4;
UPDATE payment_methods SET cvv = '654' WHERE id = 5;
UPDATE payment_methods SET cvv = '987' WHERE id = 6;
UPDATE payment_methods SET cvv = '147' WHERE id = 7;
UPDATE payment_methods SET cvv = '147' WHERE id = 8;
UPDATE payment_methods SET cvv = '147' WHERE id = 9;
UPDATE payment_methods SET cvv = '147' WHERE id = 10;
