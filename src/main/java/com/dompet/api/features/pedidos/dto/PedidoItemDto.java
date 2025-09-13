package com.dompet.api.features.pedidos.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PedidoItemDto(
  @JsonProperty("id") Long id,
  Long produtoId,
  String nome,
  // manter campo interno preco, mas expor precoUnitario principal
  BigDecimal preco,
  Integer quantidade,
  BigDecimal subtotal
) {
  @JsonProperty("itemId") public Long itemIdAlias() { return id; }
  @JsonProperty("nomeProduto") public String legacyNomeProduto() { return nome; }
  // Campo principal esperado pelo front
  @JsonProperty("precoUnitario") public BigDecimal precoUnitario() { return preco; }
  @JsonProperty("preco") public BigDecimal precoAliasBridging() { return preco; }
  @JsonProperty("lineTotal") public BigDecimal lineTotalAlias() { return subtotal; }
}
