// Carrinho.java
// Entidade do carrinho que agrupa itens, dono (usuario) e mantém timestamps.
// Contém lógica para calcular total somando subtotais dos itens.
package com.dompet.api.features.carrinho.domain;

import com.dompet.api.features.usuarios.domain.Usuarios;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Entidade Carrinho com itens e total calculado.
 * - usuario: dono do carrinho
 * - status: ABERTO/FECHADO
 * - itens: OneToMany com cascade e orphanRemoval (itens são persistidos/removidos junto)
 * - createdAt/updatedAt: timestamps via @PrePersist/@PreUpdate
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Carrinho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull(message = "usuario obrigatorio")
    private Usuarios usuario;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "status obrigatorio")
    private CartStatus status = CartStatus.ABERTO;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    void onCreate() { this.createdAt = this.updatedAt = new Date(); }

    @PreUpdate
    void onUpdate() { this.updatedAt = new Date(); }

    /** Soma os subtotais dos itens com arredondamento de 2 casas. */
    public BigDecimal getTotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Igualdade baseada em id; entidades transient (id null) não são consideradas iguais
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrinho carrinho = (Carrinho) o;
        return id != null && id.equals(carrinho.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
