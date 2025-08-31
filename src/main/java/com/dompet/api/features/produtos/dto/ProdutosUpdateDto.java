package com.dompet.api.features.produtos.dto;

import com.dompet.api.features.produtos.domain.Categorias;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO de atualização de produto (PUT /produtos/{id}), todos os campos opcionais. */
public record ProdutosUpdateDto(
    String nome,
    String descricao,
    @Digits(integer = 8, fraction = 2, message = "preço deve ter no máximo 8 dígitos e 2 casas decimais") @PositiveOrZero(message = "preço não pode ser negativo") BigDecimal preco,
    @PositiveOrZero(message = "estoque não pode ser negativo") Integer estoque,
    String imagemUrl,
    Categorias categoria,
    Boolean ativo,
    @Size(max = 60) String sku
) {}
