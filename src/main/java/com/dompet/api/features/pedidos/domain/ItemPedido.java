package com.dompet.api.features.pedidos.domain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import com.dompet.api.features.pedidos.domain.Pedidos;
import com.dompet.api.features.produtos.domain.Produtos; 

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
    private BigDecimal precoUnitario;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produtos produto;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedidos pedido;

    private BigDecimal subtotal;

    public BigDecimal calcularSubtotal() {
        if (quantidade != null && precoUnitario != null) {
            subtotal = precoUnitario.multiply(new BigDecimal(quantidade));
        }
        return subtotal;
    }   
}
