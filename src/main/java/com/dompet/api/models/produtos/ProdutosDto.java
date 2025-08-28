package com.dompet.api.models.produtos;

import java.math.BigDecimal;

import com.dompet.api.models.categorias.Categorias;

public record ProdutosDto(
    String nome,
    String descricao,
    BigDecimal preco,
    Integer estoque,
    String imagemUrl,
    Boolean ativo,
    Categorias categoria
    
) {

}
