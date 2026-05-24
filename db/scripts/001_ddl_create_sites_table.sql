--liquibase formatted sql

--changeset url-shortcut:create-sites-table
CREATE TABLE IF NOT EXISTS sites (
        id BIGSERIAL PRIMARY KEY,
        domain VARCHAR(250) NOT NULL,
        login VARCHAR(50) NOT NULL,
        password VARCHAR(50) NOT NULL,
        role VARCHAR(50) NOT NULL,
        CONSTRAINT password_length_check CHECK (LENGTH(password) >= 6),
        CONSTRAINT site_login_unique UNIQUE(domain, login)
    );

--rollback DROP TABLE IF EXISTS sites;