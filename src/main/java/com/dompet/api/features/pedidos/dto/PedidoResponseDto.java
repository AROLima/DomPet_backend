package com.dompet.api.features.pedidos.dto;

import com.dompet.api.shared.endereco.EnderecoDto;
import java.math.BigDecimal;
import java.util.*;

public record PedidoResponseDto(
    Long id,
    String status,
    EnderecoDto enderecoEntrega,
    List<ItemDto> itens,
    BigDecimal total,
    Date createdAt
) {
    public record ItemDto(
        Long produtoId,
        String nome,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal
    ) {}
}
