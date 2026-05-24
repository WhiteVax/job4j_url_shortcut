--liquibase formatted sql

--changeset url-shortcut:create-sites-table
CREATE TABLE IF NOT EXISTS urls (
    id BIGSERIAL PRIMARY KEY,
    site_id BIGINT REFERENCES sites(id) ON DELETE CASCADE,
    original_url VARCHAR(255) NOT NULL,
    code VARCHAR(100) UNIQUE,
    total INTEGER DEFAULT 0
);

--rollback DROP TABLE IF EXISTS urls;