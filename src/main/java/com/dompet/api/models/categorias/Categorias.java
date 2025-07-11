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
    private List<Produtos> produtos;

}
