package com.dompet.api.features.carrinho.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resposta resumida do carrinho (para endpoints /carrinho).
 * - id: id do carrinho do usuário
 * - itens: lista de itens com detalhes e subtotal
 * - total: valor total com escala monetária (2 casas)
 */
public record CarrinhoDto(
    Long id,
    List<ItemCarrinhoDto> itens,
    BigDecimal total
) {}
// DTO usado por endpoints de carrinho (carrinho por id) com subtotais e total calculado.
