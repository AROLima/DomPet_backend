package com.dompet.api.models.pagamento;

import com.dompet.api.models.pagamento.enums.StatusPagamento;
import com.dompet.api.models.pagamento.enums.TipoPagamento;
import com.dompet.api.models.pedidos.Pedidos;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;
}
