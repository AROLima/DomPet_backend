-- V12: Add column observacoes to pedidos (nullable) if missing
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='pedidos') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='pedidos' AND column_name='observacoes') THEN
            EXECUTE 'ALTER TABLE pedidos ADD COLUMN observacoes VARCHAR(1000)';
        END IF;
    END IF;
END$$;
