-- V4: Normalização de nomes para alinhar entidades JPA e schema
-- Ajustes:
-- 1. Renomear tabela pedido_itens -> item_pedido (naming padrão para ItemPedido)
-- 2. Renomear colunas de pedidos:
--    valor_total -> total
--    endereco_* -> remover prefixo (rua->logradouro etc) conforme Endereco embutido
--    adicionar coluna ativo (soft delete) se não existir
-- 3. Adicionar colunas de endereço em usuarios (Endereco embutido) se não existirem.

DO $$
BEGIN
    -- 1. Renomear tabela de itens do pedido se necessário
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='pedido_itens')
       AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='item_pedido') THEN
        EXECUTE 'ALTER TABLE pedido_itens RENAME TO item_pedido';
    END IF;

    -- 2. Ajustar tabela pedidos
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='pedidos') THEN
        -- valor_total -> total
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='valor_total')
           AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='total') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN valor_total TO total';
        END IF;
        -- Endereço: logradouro, numero, complemento, bairro, cidade, estado, cep
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='endereco_logradouro') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN endereco_logradouro TO logradouro';
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='endereco_numero') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN endereco_numero TO numero';
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='endereco_complemento') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN endereco_complemento TO complemento';
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='endereco_bairro') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN endereco_bairro TO bairro';
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='endereco_cidade') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN endereco_cidade TO cidade';
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='endereco_estado') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN endereco_estado TO estado';
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='endereco_cep') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN endereco_cep TO cep';
        END IF;
        -- Coluna ativo
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='ativo') THEN
            EXECUTE 'ALTER TABLE pedidos ADD COLUMN ativo BOOLEAN DEFAULT TRUE';
        END IF;
    END IF;

    -- 3. Endereço embutido em usuarios: logradouro, numero, complemento, bairro, cidade, estado, cep
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='usuarios') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='logradouro') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN logradouro VARCHAR(255)';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='numero') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN numero VARCHAR(50)';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='complemento') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN complemento VARCHAR(255)';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='bairro') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN bairro VARCHAR(255)';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='cidade') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN cidade VARCHAR(255)';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='estado') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN estado VARCHAR(2)';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='cep') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN cep VARCHAR(20)';
        END IF;
    END IF;
END$$;
