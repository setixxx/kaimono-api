CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL CHECK (base_price >= 0),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT uq_products_public_id UNIQUE (public_id)
    );

CREATE INDEX idx_products_is_available ON products(is_available);

CREATE INDEX idx_products_name ON products USING gin(to_tsvector('english', name));

CREATE TRIGGER trg_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();