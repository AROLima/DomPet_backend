-- V8: Remove coluna preco_unitario da tabela item_carrinho
-- A aplicação calcula subtotal dinamicamente (produto.preco * quantidade),
-- portanto a coluna tornou-se redundante e estava causando erro de INSERT.
-- Torna o schema coerente com a entidade ItemCarrinho (que não possui o campo).

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'item_carrinho' AND column_name = 'preco_unitario'
    ) THEN
        ALTER TABLE item_carrinho DROP COLUMN preco_unitario;
    END IF;
END$$;
