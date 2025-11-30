CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(120) NOT NULL,
    surname VARCHAR(120),
    phone VARCHAR(12) NOT NULL,
    email TEXT NOT NULL,
    birthday DATE,
    gender VARCHAR NOT NULL DEFAULT 'Male' CHECK (gender IN ('Male','Female')),
    password_hash TEXT NOT NULL,
    CONSTRAINT uq_users_public_id UNIQUE (public_id),
    CONSTRAINT uq_users_email UNIQUE (email)
);