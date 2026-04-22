
ALTER TABLE tb_master_keys ADD COLUMN status VARCHAR(20);

UPDATE tb_master_keys SET status = 'CURRENT' WHERE is_active = true;
UPDATE tb_master_keys SET status = 'DEPRECATED' WHERE is_active = false;


ALTER TABLE tb_master_keys ALTER COLUMN status SET NOT NULL;


ALTER TABLE tb_master_keys DROP COLUMN is_active;