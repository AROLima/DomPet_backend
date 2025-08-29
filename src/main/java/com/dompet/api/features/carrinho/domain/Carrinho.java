package com.dompet.api.features.carrinho.domain;

import com.dompet.api.features.usuarios.domain.Usuarios;
import jakarta.persistence.*;
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
    private Usuarios usuario;

    @Enumerated(EnumType.STRING)
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
}
