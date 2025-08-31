package com.dompet.api.features.produtos.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.dompet.api.features.produtos.domain.Categorias;

/** DTO interno para agregação de dados usados na entidade e no service. */
public record ProdutosDto(
    @NotBlank(message = "nome é obrigatório") String nome,
    String descricao,
    @NotNull(message = "preço é obrigatório") @Digits(integer = 8, fraction = 2, message = "preço deve ter no máximo 8 dígitos e 2 casas decimais") @PositiveOrZero(message = "preço não pode ser negativo") BigDecimal preco,
    @NotNull(message = "estoque é obrigatório") @PositiveOrZero(message = "estoque não pode ser negativo") Integer estoque,
    String imagemUrl,
    Boolean ativo,
    @NotNull(message = "categoria é obrigatória") Categorias categoria,
    String sku
) {}
