package com.dompet.api.features.carrinho.dto;

import jakarta.validation.constraints.*;

/** Corpo para adicionar item ao carrinho (/cart/items). */
public record CartItemAddDto(
    @NotNull Long produtoId,
    @NotNull @Positive Integer quantidade
) {}
