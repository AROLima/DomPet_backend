-- V3: Ajuste de nomenclatura da tabela de itens do carrinho
-- O erro em produção: missing table [item_carrinho]
-- A V1 criou 'carrinho_item'. As entidades sem @Table usam estratégia padrão (camelCase -> snake_case) gerando 'item_carrinho'.
-- Solução: renomear a tabela mantendo dados.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables 
        WHERE table_schema = 'public' AND table_name = 'carrinho_item'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.tables 
        WHERE table_schema = 'public' AND table_name = 'item_carrinho'
    ) THEN
        EXECUTE 'ALTER TABLE carrinho_item RENAME TO item_carrinho';
    END IF;
END$$;

-- Índices antigos permanecem; opcionalmente poderíamos renomear também se necessário.
