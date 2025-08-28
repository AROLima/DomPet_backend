package com.dompet.api.models.produtos;

import java.math.BigDecimal;

import com.dompet.api.models.categorias.Categorias;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Produtos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Lob // se for curta, pode remover
    private String descricao;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    private Integer estoque;
    private String imagemUrl;

    @Enumerated(EnumType.STRING)
    private Categorias categoria;

    private Boolean ativo = true;

    // Construtor que recebe o DTO (como você queria)
    public Produtos(ProdutosDto dados){
        this.nome = dados.nome();
        this.descricao = dados.descricao();
        this.preco = dados.preco();
        this.estoque = dados.estoque();
        this.imagemUrl = dados.imagemUrl();
        this.categoria = dados.categoria();
        this.ativo = dados.ativo() != null ? dados.ativo() : Boolean.TRUE;
    }

    public void atualizarInformacoes(ProdutosDto dados) {
        if (dados.nome() != null)        this.nome = dados.nome();
        if (dados.descricao() != null)   this.descricao = dados.descricao();
        if (dados.preco() != null)       this.preco = dados.preco();
        if (dados.estoque() != null)     this.estoque = dados.estoque();
        if (dados.imagemUrl() != null)   this.imagemUrl = dados.imagemUrl();
        if (dados.categoria() != null)   this.categoria = dados.categoria();
        if (dados.ativo() != null)       this.ativo = dados.ativo();
    }

    // helpers
    public void excluir()   { this.ativo = false; }
    public void restaurar() { this.ativo = true;  }
}
