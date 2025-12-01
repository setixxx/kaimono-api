CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_id BIGINT,
    rating SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    CONSTRAINT uq_review_per_order_product UNIQUE (user_id, product_id, order_id)
    );

CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

CREATE TRIGGER trg_reviews_updated_at
    BEFORE UPDATE ON reviews
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION validate_review_after_delivery()
RETURNS TRIGGER AS $$
DECLARE
v_delivery_status VARCHAR(50);
    v_order_status_code VARCHAR(50);
BEGIN
SELECT d.status, os.code
INTO v_delivery_status, v_order_status_code
FROM deliveries d
         JOIN orders o ON d.order_id = o.id
         JOIN order_statuses os ON o.status_id = os.id
WHERE d.order_id = NEW.order_id;

IF v_order_status_code != 'delivered' OR v_delivery_status != 'delivered' THEN
        RAISE EXCEPTION 'Отзыв можно оставить только после доставки заказа';
END IF;

    IF NOT EXISTS (
        SELECT 1 FROM order_items
        WHERE order_id = NEW.order_id
        AND product_id = NEW.product_id
    ) THEN
        RAISE EXCEPTION 'Товар не найден в указанном заказе';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_review_after_delivery
    BEFORE INSERT ON reviews
    FOR EACH ROW
    WHEN (NEW.order_id IS NOT NULL)
    EXECUTE FUNCTION validate_review_after_delivery();