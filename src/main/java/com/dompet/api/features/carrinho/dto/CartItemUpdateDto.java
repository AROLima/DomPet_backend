package com.dompet.api.features.carrinho.dto;

import jakarta.validation.constraints.*;

/** Corpo para atualizar quantidade absoluta de um item (/cart/items/{itemId}). */
public record CartItemUpdateDto(
    @NotNull @Min(0) Integer quantidade
) {}
