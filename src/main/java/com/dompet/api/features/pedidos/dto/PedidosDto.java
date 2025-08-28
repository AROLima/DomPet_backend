package com.dompet.api.features.pedidos.dto;

import java.util.List;
import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.pedidos.domain.ItemPedido;
import com.dompet.api.features.pedidos.domain.StatusPedido;

public record PedidosDto(
    Endereco enderecoEntrega,
    int quantidade,
    StatusPedido status,
    List<ItemPedido> itens
) {}
