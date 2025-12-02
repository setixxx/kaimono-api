ALTER TABLE wishlist
    ADD COLUMN product_size_id BIGINT;

ALTER TABLE wishlist
    ADD CONSTRAINT fk_wishlist_product_size
        FOREIGN KEY (product_size_id)
            REFERENCES product_sizes(id)
            ON DELETE SET NULL;

CREATE INDEX idx_wishlist_product_size ON wishlist(product_size_id);