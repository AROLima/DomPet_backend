package com.dompet.api.features.pedidos.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.dompet.api.features.pedidos.domain.PedidoStatus;

public record PedidoDto(
  Long id,
  PedidoStatus status,
  BigDecimal total,
  String observacoes,
  String enderecoRua,
  String enderecoNumero,
  String enderecoBairro,
  String enderecoCep,
  String enderecoCidade,
  OffsetDateTime createdAt,
  List<PedidoItemDto> itens
) {
  @JsonProperty("items") public List<PedidoItemDto> itemsAlias() { return itens; }
  @JsonProperty("created_at") public OffsetDateTime createdAtSnake() { return createdAt; }

  // Objeto de endereço aninhado para compatibilidade com frontend
  @JsonProperty("enderecoEntrega")
  public java.util.Map<String,Object> enderecoEntrega() {
    return java.util.Map.of(
      "rua", enderecoRua == null ? "" : enderecoRua,
      "numero", enderecoNumero == null ? "" : enderecoNumero,
      "bairro", enderecoBairro == null ? "" : enderecoBairro,
      "cep", enderecoCep == null ? "" : enderecoCep,
      "cidade", enderecoCidade == null ? "" : enderecoCidade
    );
  }
}
