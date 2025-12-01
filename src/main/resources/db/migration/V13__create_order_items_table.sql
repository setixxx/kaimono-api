CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_size_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price_at_purchase DECIMAL(10,2) NOT NULL CHECK (price_at_purchase >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),

    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_items_size FOREIGN KEY (product_size_id) REFERENCES product_sizes(id) ON DELETE RESTRICT
    );

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

CREATE OR REPLACE FUNCTION calculate_order_item_subtotal()
RETURNS TRIGGER AS $$
BEGIN
    NEW.subtotal = NEW.price_at_purchase * NEW.quantity;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_calculate_order_item_subtotal
    BEFORE INSERT OR UPDATE ON order_items
                         FOR EACH ROW
                         EXECUTE FUNCTION calculate_order_item_subtotal();