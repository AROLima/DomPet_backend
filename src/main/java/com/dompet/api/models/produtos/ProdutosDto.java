package com.dompet.api.models.produtos;

public record ProdutosDto(
    Long id,
    String nome,
    String descricao,
    double preco,
    Integer estoque,
    String imagemUrl
) {}
