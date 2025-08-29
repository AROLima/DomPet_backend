package com.dompet.api.features.carrinho.dto;

import java.math.BigDecimal;
import java.util.List;

/** Resposta resumida do carrinho (para endpoints /carrinho). */
public record CarrinhoDto(
    Long id,
    List<ItemCarrinhoDto> itens,
    BigDecimal total
) {}
