ALTER TABLE tb_secret
    ADD COLUMN customer_id UUID NOT NULL;


CREATE INDEX idx_secret_customer ON tb_secret(customer_id);


ALTER TABLE tb_secret
    ADD CONSTRAINT fk_secret_customer
        FOREIGN KEY (customer_id) REFERENCES tb_user(id);