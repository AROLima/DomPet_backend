package com.dompet.api.features.carrinho.domain;

import com.dompet.api.features.produtos.domain.Produtos;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class ItemCarrinho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Carrinho carrinho;

    @ManyToOne(optional = false)
    private Produtos produto;

    @Column(nullable = false)
    private Integer quantidade;

    public BigDecimal getSubtotal() {
        if (produto == null || produto.getPreco() == null || quantidade == null) return null;
        return produto.getPreco()
                .multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
