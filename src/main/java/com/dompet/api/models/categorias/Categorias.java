package com.dompet.api.models.categorias;

import java.util.List;

import com.dompet.api.models.produtos.Produtos;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Categorias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Produtos> produtos;


    public Categorias(CategoriasDto dados) {
        this.nome = dados.nome();

    }
}
