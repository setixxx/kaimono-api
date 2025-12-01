CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    city VARCHAR(100) NOT NULL,
    street VARCHAR(200) NOT NULL,
    house VARCHAR(20) NOT NULL,
    apartment VARCHAR(20),
    zip_code VARCHAR(20) NOT NULL,
    additional_info TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX idx_addresses_user_id ON addresses(user_id);

CREATE INDEX idx_addresses_user_default ON addresses(user_id, is_default) WHERE is_default = TRUE;

CREATE OR REPLACE FUNCTION ensure_single_default_address()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_default = TRUE THEN
UPDATE addresses
SET is_default = FALSE
WHERE user_id = NEW.user_id
  AND id != NEW.id
          AND is_default = TRUE;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ensure_single_default_address
    BEFORE INSERT OR UPDATE ON addresses
                         FOR EACH ROW
                         EXECUTE FUNCTION ensure_single_default_address();