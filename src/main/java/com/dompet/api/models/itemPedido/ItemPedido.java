package com.dompet.api.models.itempedido;
import jakarta.persistence.*;
import lombok.*;

import com.dompet.api.models.pedidos.Pedidos;
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

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produtos produto;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedidos pedido;
}
