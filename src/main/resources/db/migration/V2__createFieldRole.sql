CREATE TABLE tb_role (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);


CREATE TABLE tb_users_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE
);

INSERT INTO tb_role (id, name) VALUES (gen_random_uuid(), 'ROLE_USER');
INSERT INTO tb_role (id, name) VALUES (gen_random_uuid(), 'ROLE_ADMIN');