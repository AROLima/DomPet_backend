// ItemCarrinho.java
// Entidade que representa uma linha no carrinho: produto + quantidade.
// Oferece método getSubtotal() que calcula preco * quantidade com arredondamento.
package com.dompet.api.features.carrinho.domain;

import com.dompet.api.features.produtos.domain.Produtos;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Item de carrinho: produto + quantidade e subtotal calculado. */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class ItemCarrinho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull(message = "carrinho obrigatorio")
    private Carrinho carrinho;

    @ManyToOne(optional = false)
    @NotNull(message = "produto obrigatorio")
    private Produtos produto;

    @Column(nullable = false)
    @NotNull(message = "quantidade obrigatoria")
    @Positive(message = "quantidade > 0")
    private Integer quantidade;

    /** preço unitário x quantidade, arredondado para 2 casas. */
    public BigDecimal getSubtotal() {
        if (produto == null || produto.getPreco() == null || quantidade == null) return null;
        return produto.getPreco()
                .multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Igualdade baseada em id; evita comparar produto/carrinho (lazy)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCarrinho that = (ItemCarrinho) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
