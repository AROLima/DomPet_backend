package com.dompet.api.models.produtos;
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
    private String descricao;
    private double preco;
    private Integer estoque;
    private String imagemUrl;

    @Enumerated
    private Categorias categoria;

    private Boolean ativo = true;
    
    

    public Produtos(ProdutosDto dados){
        this.nome = dados.nome();
        this.descricao = dados.descricao();
        this.preco = dados.preco();
        this.estoque = dados.estoque();
        this.imagemUrl = dados.imagemUrl();
    }

    public void atualizarInformacoes(ProdutosDto dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }
        if (dados.preco() != 0) {
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
