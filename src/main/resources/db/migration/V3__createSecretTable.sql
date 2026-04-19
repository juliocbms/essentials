CREATE TABLE tb_secret (
    id UUID PRIMARY KEY,
    secret_name VARCHAR(255) NOT NULL,
    secret_provider VARCHAR(255) NOT NULL,
    secret_encrypted_value TEXT NOT NULL UNIQUE,
    secret_iv VARCHAR(255) NOT NULL,
    secret_key_version INTEGER NOT NULL,
    secret_key_status BOOLEAN NOT NULL DEFAULT TRUE,
    secret_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    secret_updated_at TIMESTAMP WITHOUT TIME ZONE,
    secret_deleted_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_secret_key_status
ON tb_secret (secret_key_status)
WHERE secret_deleted_at IS NULL;