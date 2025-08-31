package com.dompet.api.features.carrinho.dto;

import jakarta.validation.constraints.*;

/**
 * Corpo para adicionar item ao carrinho (/cart/items).
 * - produtoId: id do produto a ser adicionado
 * - quantidade: quantidade positiva requerida */
public record CartItemAddDto(
    @NotNull Long produtoId,
    @NotNull @Positive Integer quantidade
) {}
// Nota: endpoint /cart/items espera este formato; front-end deve validar localmente antes de enviar.
