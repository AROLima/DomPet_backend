package com.dompet.api.features.pedidos.domain;

/**
 * Fluxo de status de pedidos usado pela aplicação. Manter esta enum pequena e
 * explícita ajuda a controlar transições de estado no serviço de pedidos.
 */
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO,
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}
