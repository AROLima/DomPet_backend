package com.dompet.api.models.itemPedido;
import jakarta.persistence.*;
import lombok.*;

import com.dompet.api.models.produtos.Produtos; 

/**
Classe que representa um item de pedido.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;
    private double precoUnitario;

    
    private Produtos produto;


    private Pedido pedido;
}
