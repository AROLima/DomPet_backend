package com.dompet.api.features.pedidos.mapper;

import java.util.stream.Collectors;
import com.dompet.api.features.pedidos.domain.Pedido;
import com.dompet.api.features.pedidos.domain.PedidoItem;
import com.dompet.api.features.pedidos.dto.PedidoDto;
import com.dompet.api.features.pedidos.dto.PedidoItemDto;

public final class PedidoMapper {
  private PedidoMapper() {}

  public static PedidoItemDto toItemDto(PedidoItem i) {
    if (i == null) return new PedidoItemDto(null, null, "", java.math.BigDecimal.ZERO, 0, java.math.BigDecimal.ZERO);
    var prod = i.getProduto();
    return new PedidoItemDto(
      i.getId(),
      prod == null ? null : prod.getId(),
      i.getNomeProduto(),
      i.getPrecoUnitario(),
      i.getQuantidade(),
      i.getSubtotal()
    );
  }

  public static PedidoDto toDto(Pedido p) {
    var itens = p.getItens();
    var itensDto = itens == null ? java.util.List.<PedidoItemDto>of() :
      itens.stream().map(PedidoMapper::toItemDto).collect(Collectors.toList());
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
      itensDto
    );
  }

  private static String nz(String v) { return v == null ? "" : v; }
}
