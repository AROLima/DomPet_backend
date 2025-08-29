package com.dompet.api.features.pedidos.dto;

import com.dompet.api.shared.endereco.EnderecoDto;
import java.math.BigDecimal;
import java.util.*;

/** Resposta de pedido com itens, total e timestamps. */
public record PedidoResponseDto(
    Long id,
    String status,
    EnderecoDto enderecoEntrega,
    List<ItemDto> itens,
    BigDecimal total,
    Date createdAt
) {
    /** Item do pedido no response. */
    public record ItemDto(
        Long produtoId,
        String nome,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal
    ) {}
}
