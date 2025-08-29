package com.dompet.api.features.pedidos.domain;

/** Fluxo de status de pedidos. */
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO,
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}
