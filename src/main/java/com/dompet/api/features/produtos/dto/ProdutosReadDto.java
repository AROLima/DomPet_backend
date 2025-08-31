package com.dompet.api.features.produtos.dto;

import com.dompet.api.features.produtos.domain.Categorias;
import java.math.BigDecimal;

/** DTO de leitura para respostas (seguro para expor ao cliente). */
public record ProdutosReadDto(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    Integer estoque,
    String imagemUrl,
    Categorias categoria,
    Boolean ativo,
    String sku
) {}
