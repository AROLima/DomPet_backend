package com.dompet.api.features.pedidos.domain;
import java.math.BigDecimal;
import java.util.*;

import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.usuarios.domain.Usuarios;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade de Pedido: representa uma compra realizada por um usuário.
 *
 * Observações de contrato:
 * - {@link itens} é a lista de {@link ItemPedido} com cascade ALL para persistência automática
 * - {@link enderecoEntrega} é um valor embutido (Embedded) para evitar entidade separada * - {@link total} representa o total do pedido e deve ser calculado antes de persistir * - {@link ativo} permite exclusão lógica sem remover registro do banco */
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

    /** Itens do pedido. Cascade ALL garante salvar/atualizar/remover junto ao pedido. */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;

    /** Flag para exclusão lógica (soft delete). */
    private Boolean ativo = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    void onCreate() { this.createdAt = this.updatedAt = new Date(); }
    @PreUpdate
    void onUpdate() { this.updatedAt = new Date(); }

    /** Marca o pedido como inativo (exclusão lógica). */
    public void excluir() { this.ativo = false; }
    // Nota: o total deve ser calculado antes de persistir para garantir consistência de relatórios.
}
