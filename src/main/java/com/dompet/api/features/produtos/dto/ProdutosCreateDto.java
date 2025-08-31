package com.dompet.api.features.produtos.dto;

import com.dompet.api.features.produtos.domain.Categorias;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO de criação de produto (POST /produtos). */
public record ProdutosCreateDto(
    @NotBlank(message = "nome é obrigatório") String nome,
    String descricao,
    @NotNull(message = "preço é obrigatório") @Digits(integer = 8, fraction = 2, message = "preço deve ter no máximo 8 dígitos e 2 casas decimais") @PositiveOrZero(message = "preço não pode ser negativo") BigDecimal preco,
    @NotNull(message = "estoque é obrigatório") @PositiveOrZero(message = "estoque não pode ser negativo") Integer estoque,
    String imagemUrl,
    @NotNull(message = "categoria é obrigatória") Categorias categoria,
    Boolean ativo,
    @Size(max = 60) String sku
) {}
