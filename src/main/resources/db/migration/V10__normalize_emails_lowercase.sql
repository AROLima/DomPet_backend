-- V10: Normaliza todos os emails para minúsculo para evitar falhas de login por case sensitivity
UPDATE usuarios SET email = LOWER(email);
-- Opcional: poderíamos adicionar uma constraint de verificação, mas já existe UNIQUE.