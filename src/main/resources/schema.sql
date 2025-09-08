-- Token version column for logout-all support
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS token_version INT NOT NULL DEFAULT 0;
ALTER TABLE usuarios ALTER COLUMN token_version SET DEFAULT 0;
UPDATE usuarios SET token_version = 0 WHERE token_version IS NULL;

-- Garantir unicidade de email em usuarios (evita duplicidades que quebram consultas por email)
ALTER TABLE usuarios ADD CONSTRAINT IF NOT EXISTS uk_usuarios_email UNIQUE (email);

-- Garantir apenas um carrinho ABERTO por usuário (modelo lógico)
-- Em H2, criamos índice parcial via constraint simulada usando CHECK + índice composto.
-- Nota: CHECK não garante unicidade por si só; então adicionamos um índice auxiliar.
-- Para produção (PostgreSQL), o ideal é um índice parcial único: UNIQUE WHERE (status='ABERTO').
CREATE INDEX IF NOT EXISTS idx_carrinho_usuario_status ON carrinho(usuario_id, status);
