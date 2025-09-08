-- V5: Ajuste final das colunas de endereço em pedidos para refletir Embeddable Endereco (rua, numero, bairro, cep, cidade, complemento)
-- Situação após V4: pedidos possui colunas: logradouro, numero, complemento, bairro, cidade, estado, cep
-- Entidade Endereco usa: rua, numero, bairro, cep, cidade, complemento
-- Ação: renomear logradouro -> rua; remover estado (não mapeado); garantir complemento existe.

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='pedidos') THEN
        -- Renomear logradouro -> rua
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='logradouro')
           AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='rua') THEN
            EXECUTE 'ALTER TABLE pedidos RENAME COLUMN logradouro TO rua';
        END IF;
        -- Remover estado se presente (não há campo correspondente no Embeddable)
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='estado') THEN
            EXECUTE 'ALTER TABLE pedidos DROP COLUMN estado';
        END IF;
        -- Garantir coluna complemento existe (já deve existir, mas por segurança)
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='complemento') THEN
            EXECUTE 'ALTER TABLE pedidos ADD COLUMN complemento VARCHAR(255)';
        END IF;
    END IF;
END$$;
