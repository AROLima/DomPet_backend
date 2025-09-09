-- V6: Ajuste colunas de endereço em usuarios para Embeddable Endereco (rua, numero, bairro, cep, cidade, complemento)
-- Após V4 podem existir colunas logradouro / estado que não casam com Embeddable.

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='usuarios') THEN
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='logradouro')
           AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='rua') THEN
            EXECUTE 'ALTER TABLE usuarios RENAME COLUMN logradouro TO rua';
        END IF;
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='estado') THEN
            EXECUTE 'ALTER TABLE usuarios DROP COLUMN estado';
        END IF;
        -- Garantir complemento existe
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='usuarios' AND column_name='complemento') THEN
            EXECUTE 'ALTER TABLE usuarios ADD COLUMN complemento VARCHAR(255)';
        END IF;
    END IF;
END$$;
