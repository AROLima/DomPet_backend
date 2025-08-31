package com.dompet.api.features.produtos.domain;
import java.math.BigDecimal;
import com.dompet.api.features.produtos.dto.ProdutosDto;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade JPA de Produtos.
 * Campos principais: nome, descrição, preço, estoque, imagem, categoria e flag ativo.
 * Possui helpers para atualização parcial e exclusão lógica (ativo=false).
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Produtos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Lob // descrição longa; se for curta, pode remover @Lob
    private String descricao;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    private Integer estoque;
    private String imagemUrl;

    @Enumerated(EnumType.STRING)
    private Categorias categoria;

    @Column(unique = true)
    private String sku;

    private Boolean ativo = true;

    // Construtor prático a partir de um DTO básico
    public Produtos(ProdutosDto dados){
        this.nome = dados.nome();
        this.descricao = dados.descricao();
        this.preco = dados.preco();
        this.estoque = dados.estoque();
        this.imagemUrl = dados.imagemUrl();
    this.categoria = dados.categoria();
        this.ativo = dados.ativo() != null ? dados.ativo() : Boolean.TRUE;
    this.sku = dados.sku();
    }

    /** Atualiza apenas campos presentes no DTO (parcial). */
    public void atualizarInformacoes(ProdutosDto dados) {
        if (dados.nome() != null)        this.nome = dados.nome();
        if (dados.descricao() != null)   this.descricao = dados.descricao();
        if (dados.preco() != null)       this.preco = dados.preco();
        if (dados.estoque() != null)     this.estoque = dados.estoque();
        if (dados.imagemUrl() != null)   this.imagemUrl = dados.imagemUrl();
    if (dados.categoria() != null)   this.categoria = dados.categoria();
        if (dados.ativo() != null)       this.ativo = dados.ativo();
    if (dados.sku() != null)         this.sku = dados.sku();
    }

    // helpers de status
    public void excluir()   { this.ativo = false; }
    public void restaurar() { this.ativo = true;  }
}
