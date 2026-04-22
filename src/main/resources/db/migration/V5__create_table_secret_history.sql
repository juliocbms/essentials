CREATE TABLE tb_secret_history (
    id UUID PRIMARY KEY,
    secret_id UUID NOT NULL,
    encrypted_value TEXT NOT NULL,
    initialization_vector TEXT NOT NULL,
    key_version INTEGER NOT NULL,
    archived_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_history_secret FOREIGN KEY (secret_id) REFERENCES tb_secret(id) ON DELETE CASCADE
);

CREATE INDEX idx_history_secret_id ON tb_secret_history(secret_id);