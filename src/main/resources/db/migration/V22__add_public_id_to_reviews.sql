ALTER TABLE reviews
    ADD COLUMN public_id UUID NOT NULL DEFAULT uuid_generate_v4();

ALTER TABLE reviews
    ADD CONSTRAINT uq_reviews_public_id UNIQUE (public_id);

CREATE INDEX idx_reviews_public_id ON reviews(public_id);
