package com.dompet.api.features.pedidos.dto;

import com.dompet.api.shared.endereco.EnderecoDto;
import jakarta.validation.constraints.NotNull;

/** Payload de checkout com endereço de entrega e metadados. */
public record CheckoutDto(
    @NotNull 
    EnderecoDto enderecoEntrega,
    String observacoes,
    String metodoPagamento
) {}
