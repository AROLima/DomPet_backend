package com.dompet.api.models.produtos;
import java.math.BigDecimal;

import com.dompet.api.models.categorias.Categorias;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Produtos {
    
 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;

    @Lob
    private String descricao;

    private BigDecimal preco;
    private Integer estoque;
    private String imagemUrl;

    @Enumerated(EnumType.STRING)
    private Categorias categoria;
    
    private Boolean ativo = true;


    //DTO
    public Produtos(ProdutosDto dados){
        this.nome = dados.nome();
        this.descricao = dados.descricao();
        this.preco = dados.preco();
        this.estoque = dados.estoque();
        this.imagemUrl = dados.imagemUrl();
        this.categoria = dados.categoria();
        this.ativo = dados.ativo();
    }

    public void atualizarInformacoes(ProdutosDto dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }
        if (dados.preco() != null && dados.preco().compareTo(BigDecimal.ZERO) != 0) {
            this.preco = dados.preco();
        }
        if (dados.estoque() != null) {
            this.estoque = dados.estoque();
        }
        if (dados.imagemUrl() != null) {
            this.imagemUrl = dados.imagemUrl();
        }
        if (dados.ativo() != null) {
            this.ativo = dados.ativo();
        }
    }
}
