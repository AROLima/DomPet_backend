-- Flyway Baseline Migration
-- Recreate current schema for PostgreSQL.
-- NOTE: Adjust column sizes/types if entities change later with incremental migrations.

-- USERS
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    token_version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_usuarios_email ON usuarios(email);

-- PRODUCTS
CREATE TABLE IF NOT EXISTS produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco NUMERIC(12,2) NOT NULL,
    estoque INT NOT NULL,
    imagem_url TEXT,
    categoria VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CART
CREATE TABLE IF NOT EXISTS carrinho (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_carrinho_usuario_status ON carrinho(usuario_id, status);
-- In Postgres we could add partial unique index for open cart (status='ABERTO') later if required.

-- CART ITEMS
CREATE TABLE IF NOT EXISTS carrinho_item (
    id BIGSERIAL PRIMARY KEY,
    carrinho_id BIGINT NOT NULL REFERENCES carrinho(id) ON DELETE CASCADE,
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_carrinho_item_carrinho ON carrinho_item(carrinho_id);

-- ORDERS
CREATE TABLE IF NOT EXISTS pedidos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL,
    valor_total NUMERIC(12,2) NOT NULL,
    endereco_logradouro VARCHAR(255) NOT NULL,
    endereco_numero VARCHAR(50) NOT NULL,
    endereco_complemento VARCHAR(255),
    endereco_bairro VARCHAR(255) NOT NULL,
    endereco_cidade VARCHAR(255) NOT NULL,
    endereco_estado VARCHAR(2) NOT NULL,
    endereco_cep VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_pedidos_usuario_status ON pedidos(usuario_id, status);

-- ORDER ITEMS
CREATE TABLE IF NOT EXISTS pedido_itens (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    nome_produto VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_pedido_itens_pedido ON pedido_itens(pedido_id);

-- SEED DATA (optional) - replicate essential initial records; keep minimal for production
INSERT INTO usuarios (nome, email, senha, role, ativo, token_version) VALUES
('Admin', 'admin@dompet.dev', '$2b$10$E.iUyby25SywbG1jyoHM7uoIDs6mhFnIPDkyTg.4g9Ove6gCp3/py', 'ADMIN', TRUE, 0)
ON CONFLICT (email) DO NOTHING;

-- Product sample seed (could be moved to a separate V2__seed_data.sql if preferred)
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Premium Cães Adultos 10kg', 'Ração completa para cães adultos, com proteínas de alta qualidade e prebióticos.', 189.90, 25, 'https://dompet-test.s3.us-east-2.amazonaws.com/royal-canin-1200x1200.png', 'RACAO', TRUE)
ON CONFLICT DO NOTHING;
