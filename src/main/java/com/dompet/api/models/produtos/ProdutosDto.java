package com.dompet.api.models.produtos;

import com.dompet.api.models.categorias.Categorias;

public record ProdutosDto(
    String nome,
    String descricao,
    double preco,
    Integer estoque,
    String imagemUrl,
    Boolean ativo,
    Categorias categoria
    
) {

}
