-- Converte pedidos com status NOVO para AGUARDANDO_PAGAMENTO alinhando novo padrão inicial
UPDATE pedidos SET status = 'AGUARDANDO_PAGAMENTO' WHERE status = 'NOVO';
