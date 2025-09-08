-- V9: Define estoque = 80 para todos os produtos atuais e garante não nulo.
-- Objetivo: padronizar estoque inicial em produção.

UPDATE produtos SET estoque = 80 WHERE estoque IS NULL OR estoque <> 80;

-- Opcional: poderia adicionar um CHECK (>==0). Mantemos simples por ora.
