
DROP INDEX IF EXISTS idx_master_key_version;


CREATE INDEX IF NOT EXISTS idx_master_keys_status ON tb_master_keys(status);


CREATE INDEX IF NOT EXISTS idx_secret_key_version ON tb_secret(secret_key_version);


CREATE INDEX IF NOT EXISTS idx_secret_name ON tb_secret(secret_name);


CREATE INDEX IF NOT EXISTS idx_secret_customer_status_name
    ON tb_secret(customer_id, secret_key_status, secret_name)
    WHERE secret_deleted_at IS NULL;


CREATE INDEX IF NOT EXISTS idx_users_roles_role_id ON tb_users_roles(role_id);


CREATE INDEX IF NOT EXISTS idx_user_name_lower ON tb_user((LOWER(name)));

-- Supports history retrieval by secret and time ordering
CREATE INDEX IF NOT EXISTS idx_history_secret_archived
    ON tb_secret_history(secret_id, archived_at DESC);
