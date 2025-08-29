package com.dompet.api.features.pedidos.domain;
import java.math.BigDecimal;
import java.util.*;

import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.usuarios.domain.Usuarios;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de Pedido: usuário, itens, endereço de entrega, status e total.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pedidos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Embedded
    private Endereco enderecoEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;

    private Boolean ativo = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    void onCreate() { this.createdAt = this.updatedAt = new Date(); }
    @PreUpdate
    void onUpdate() { this.updatedAt = new Date(); }

    /** Exclusão lógica do pedido. */
    public void excluir() { this.ativo = false; }
}
