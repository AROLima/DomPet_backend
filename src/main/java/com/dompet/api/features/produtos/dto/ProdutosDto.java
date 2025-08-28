package com.dompet.api.features.produtos.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.dompet.api.features.produtos.domain.Categorias;

public record ProdutosDto(
    @NotBlank String nome,
    String descricao,
    @NotNull @Digits(integer = 8, fraction = 2) @PositiveOrZero BigDecimal preco,
    @NotNull @PositiveOrZero Integer estoque,
    String imagemUrl,
    Boolean ativo,
    @NotNull Categorias categoria
) {}
