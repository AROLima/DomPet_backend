package com.dompet.api.models.produtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.dompet.api.models.categorias.Categorias;

public record ProdutosDto(
    @NotBlank String nome,
    String descricao,
    @NotNull @Digits(integer = 8, fraction = 2) @PositiveOrZero BigDecimal preco,
    @NotNull @PositiveOrZero Integer estoque,
    String imagemUrl,
    Boolean ativo,
    @NotNull Categorias categoria
) {}
