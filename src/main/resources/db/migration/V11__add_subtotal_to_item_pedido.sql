-- V11: Adiciona coluna subtotal à tabela item_pedido e popula valores existentes
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='item_pedido') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='item_pedido' AND column_name='subtotal') THEN
            EXECUTE 'ALTER TABLE item_pedido ADD COLUMN subtotal NUMERIC(12,2)';
        END IF;
        -- Preenche subtotal para linhas onde está null
        EXECUTE 'UPDATE item_pedido SET subtotal = (preco_unitario * quantidade) WHERE subtotal IS NULL';
        -- Opcional: tornar NOT NULL após popular (somente se todas preenchidas)
        IF NOT EXISTS (
            SELECT 1 FROM item_pedido WHERE subtotal IS NULL
        ) THEN
            EXECUTE 'ALTER TABLE item_pedido ALTER COLUMN subtotal SET NOT NULL';
        END IF;
    END IF;
END$$;