package com.dompet.api.models.pedidos;

import com.dompet.api.models.endereco.Endereco;

public record PedidosDto(
    Endereco enderecoEntrega,
    int quantidade
) {
    
}
