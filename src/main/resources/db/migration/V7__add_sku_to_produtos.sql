-- V7: Add SKU column to produtos, backfill, and enforce uniqueness.
-- This addresses Hibernate validation error: missing column [sku] in table [produtos].
-- Idempotent: checks existence before altering.

-- 1. Add column if not exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'produtos' AND column_name = 'sku'
    ) THEN
        ALTER TABLE produtos ADD COLUMN sku VARCHAR(100);
    END IF;
END$$;

-- 2. Backfill existing rows with deterministic SKU pattern
UPDATE produtos 
SET sku = 'SKU-' || LPAD(id::text, 6, '0')
WHERE sku IS NULL;

-- 3. Create unique index (separate from constraint for easier IF NOT EXISTS logic)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE schemaname = 'public' AND indexname = 'uk_produtos_sku'
    ) THEN
        CREATE UNIQUE INDEX uk_produtos_sku ON produtos(sku);
    END IF;
END$$;

-- 4. Optionally set NOT NULL if all rows populated (keeps flexibility if entity not marked nullable=false yet)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'produtos' AND column_name = 'sku' AND is_nullable = 'YES'
    ) AND NOT EXISTS (
        SELECT 1 FROM produtos WHERE sku IS NULL
    ) THEN
        ALTER TABLE produtos ALTER COLUMN sku SET NOT NULL;
    END IF;
END$$;
