CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_status FOREIGN KEY (status_id) REFERENCES order_statuses(id) ON DELETE RESTRICT
    );

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status_id);
CREATE INDEX idx_orders_user_status ON orders(user_id, status_id);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

CREATE TRIGGER trg_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();