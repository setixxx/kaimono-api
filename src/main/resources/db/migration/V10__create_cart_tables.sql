CREATE TABLE IF NOT EXISTS cart (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX idx_cart_user ON cart(user_id);

CREATE TRIGGER trg_cart_updated_at
    BEFORE UPDATE ON cart
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_size_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    added_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_size FOREIGN KEY (product_size_id) REFERENCES product_sizes(id) ON DELETE CASCADE,
    CONSTRAINT uq_cart_item UNIQUE (cart_id, product_id, product_size_id)
    );

CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product ON cart_items(product_id);

CREATE OR REPLACE FUNCTION ensure_cart_exists()
RETURNS TRIGGER AS $$
DECLARE
v_user_id BIGINT;
BEGIN
SELECT user_id INTO v_user_id FROM cart WHERE id = NEW.cart_id;

IF v_user_id IS NULL THEN
        RAISE EXCEPTION 'Cart not found';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ensure_cart_exists
    BEFORE INSERT ON cart_items
    FOR EACH ROW
    EXECUTE FUNCTION ensure_cart_exists();