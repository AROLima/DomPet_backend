package com.dompet.api.features.carrinho.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDto(
    Long id,
    List<ItemDto> itens,
    BigDecimal total
) {
    public record ItemDto(
        Long itemId,
        Long produtoId,
        String nome,
        BigDecimal preco,
        Integer quantidade,
        BigDecimal subtotal
    ) {}
}
