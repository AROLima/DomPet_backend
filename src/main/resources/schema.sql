-- Token version column for logout-all support
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS token_version INT NOT NULL DEFAULT 0;
ALTER TABLE usuarios ALTER COLUMN token_version SET DEFAULT 0;
UPDATE usuarios SET token_version = 0 WHERE token_version IS NULL;
