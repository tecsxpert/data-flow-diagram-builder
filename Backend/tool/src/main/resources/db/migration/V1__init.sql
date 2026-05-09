-- V1: Full consolidated schema init (idempotent)

CREATE TABLE IF NOT EXISTS dfd_records (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50),
    priority    VARCHAR(50),
    deleted     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_status     ON dfd_records(status);
CREATE INDEX IF NOT EXISTS idx_created_at ON dfd_records(created_at);

CREATE TABLE IF NOT EXISTS users (
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS diagrams (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    role        VARCHAR(50),
    description TEXT,
    user_email  VARCHAR(100),
    deadline    TIMESTAMP,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_log (
    id         SERIAL PRIMARY KEY,
    action     VARCHAR(50)  NOT NULL,
    entity_id  BIGINT       NOT NULL,
    old_value  TEXT,
    new_value  TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);