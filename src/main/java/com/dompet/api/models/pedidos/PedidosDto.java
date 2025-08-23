package com.dompet.api.models.pedidos;

import java.util.List;

import com.dompet.api.models.endereco.Endereco;
import com.dompet.api.models.itempedido.ItemPedido;
import com.dompet.api.models.pedidos.enums.StatusPedido;

public record PedidosDto(
    Endereco enderecoEntrega,
    int quantidade,
    StatusPedido status,
    List<ItemPedido> itens
) {
    
}
