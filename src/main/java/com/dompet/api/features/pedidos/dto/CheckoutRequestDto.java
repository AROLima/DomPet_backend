package com.dompet.api.features.pedidos.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequestDto(
  @NotBlank String rua,
  @NotBlank String numero,
  @NotBlank String bairro,
  @NotBlank String cep,
  @NotBlank String cidade,
  String observacoes,
  String metodoPagamento
) {}
