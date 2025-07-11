package com.dompet.api.models.produtos;

public record ProdutosDto(
    String nome,
    String descricao,
    double preco,
    Integer estoque,
    String imagemUrl
) {}
