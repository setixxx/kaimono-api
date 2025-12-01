CREATE TABLE IF NOT EXISTS product_sizes (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    size VARCHAR(20) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    price_modifier DECIMAL(10,2) DEFAULT 0 CHECK (price_modifier >= 0),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_product_sizes_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uq_product_size UNIQUE (product_id, size)
    );

CREATE INDEX idx_product_sizes_product ON product_sizes(product_id);
CREATE INDEX idx_product_sizes_stock ON product_sizes(product_id, stock_quantity);

CREATE TRIGGER trg_product_sizes_updated_at
    BEFORE UPDATE ON product_sizes
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();