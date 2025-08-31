// CartResponseDto.java
// DTO de resposta do endpoint /cart que inclui itens com ids para operações PATCH/DELETE.
package com.dompet.api.features.carrinho.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de resposta detalhada do carrinho do usuário autenticado (/cart).
 * Inclui o id do item para operações de update/delete por item.
 */
public record CartResponseDto(
    Long id,
    List<ItemDto> itens,
    BigDecimal total
) {
    /** Item com id próprio para PATCH/DELETE /cart/items/{itemId}. */
    public record ItemDto(
        Long itemId,
        Long produtoId,
        String nome,
        BigDecimal preco,
        Integer quantidade,
        BigDecimal subtotal
    ) {}
}
