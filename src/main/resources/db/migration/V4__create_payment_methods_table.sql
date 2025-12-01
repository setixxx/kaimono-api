CREATE TABLE IF NOT EXISTS payment_methods (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_number_last4 VARCHAR(4) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    expiry_month SMALLINT NOT NULL CHECK (expiry_month >= 1 AND expiry_month <= 12),
    expiry_year SMALLINT NOT NULL CHECK (expiry_year >= 2024),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_payment_methods_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX idx_payment_methods_user_id ON payment_methods(user_id);

CREATE INDEX idx_payment_methods_user_default ON payment_methods(user_id, is_default) WHERE is_default = TRUE;

CREATE OR REPLACE FUNCTION ensure_single_default_payment_method()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_default = TRUE THEN
UPDATE payment_methods
SET is_default = FALSE
WHERE user_id = NEW.user_id
  AND id != NEW.id
          AND is_default = TRUE;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ensure_single_default_payment_method
    BEFORE INSERT OR UPDATE ON payment_methods
                         FOR EACH ROW
                         EXECUTE FUNCTION ensure_single_default_payment_method();