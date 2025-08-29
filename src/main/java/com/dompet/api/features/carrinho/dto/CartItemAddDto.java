package com.dompet.api.features.carrinho.dto;

import jakarta.validation.constraints.*;

public record CartItemAddDto(
    @NotNull Long produtoId,
    @NotNull @Positive Integer quantidade
) {}
