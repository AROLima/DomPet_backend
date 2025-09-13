package com.dompet.api.features.pedidos.mapper;

import java.util.stream.Collectors;
import com.dompet.api.features.pedidos.domain.Pedido;
import com.dompet.api.features.pedidos.domain.PedidoItem;
import com.dompet.api.features.pedidos.dto.PedidoDto;
import com.dompet.api.features.pedidos.dto.PedidoItemDto;

public final class PedidoMapper {
  private PedidoMapper() {}

  public static PedidoItemDto toItemDto(PedidoItem i) {
    return new PedidoItemDto(
      i.getId(),
      i.getProduto().getId(),
      i.getNomeProduto(),
      i.getPrecoUnitario(),
      i.getQuantidade(),
      i.getSubtotal()
    );
  }

  public static PedidoDto toDto(Pedido p) {
    return new PedidoDto(
      p.getId(),
      p.getStatus(),
      p.getTotal(),
      nz(p.getObservacoes()),
      nz(p.getEnderecoRua()),
      nz(p.getEnderecoNumero()),
      nz(p.getEnderecoBairro()),
      nz(p.getEnderecoCep()),
      nz(p.getEnderecoCidade()),
      p.getCreatedAt(),
      p.getItens().stream().map(PedidoMapper::toItemDto).collect(Collectors.toList())
    );
  }

  private static String nz(String v) { return v == null ? "" : v; }
}
