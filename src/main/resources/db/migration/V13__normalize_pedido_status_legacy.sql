-- V13: Normalizar valores de status de pedidos legados
-- Adiciona suporte ao novo enum AGUARDANDO_PAGAMENTO garantindo que valores divergentes sejam alinhados.
-- Se existirem variações comuns (ex: 'AGUARDANDO-PAGAMENTO', 'AGUARDANDO PAGAMENTO'), converte.

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='pedidos') THEN
    -- Unificar variantes para AGUARDANDO_PAGAMENTO
    UPDATE pedidos SET status='AGUARDANDO_PAGAMENTO'
      WHERE status IN ('AGUARDANDO-PAGAMENTO','AGUARDANDO PAGAMENTO','PENDENTE','PENDENTE_PAGAMENTO');
    -- Opcional: mapear 'NOVO' ou outros para AGUARDANDO se desejar (não aplicado automaticamente).
  END IF;
END$$;
