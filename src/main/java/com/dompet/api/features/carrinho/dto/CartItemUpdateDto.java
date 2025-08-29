package com.dompet.api.features.carrinho.dto;

import jakarta.validation.constraints.*;

public record CartItemUpdateDto(
    @NotNull @Min(0) Integer quantidade
) {}
