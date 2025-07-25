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
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categorias categoria;

    public Produtos(ProdutosDto dados){
        this.nome = dados.nome();
        this.descricao = dados.descricao();
        this.preco = dados.preco();
        this.estoque = dados.estoque();
        this.imagemUrl = dados.imagemUrl();
    }
}
