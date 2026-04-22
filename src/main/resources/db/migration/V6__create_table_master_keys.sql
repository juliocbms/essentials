CREATE TABLE tb_master_keys (
    id UUID PRIMARY KEY,
    version INTEGER UNIQUE NOT NULL,
    encrypted_value TEXT NOT NULL,
    initialization_vector TEXT NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);


CREATE INDEX idx_master_key_version ON tb_master_keys(version);