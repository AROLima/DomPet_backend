package com.dompet.api.models.pedidos;
import java.util.List;

import com.dompet.api.models.endereco.Endereco;
import com.dompet.api.models.pedidos.enums.*;
import com.dompet.api.models.usuarios.Usuarios;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity


public class Pedidos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Usuarios usuario;

    
    
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    @Embedded
    private Endereco enderecoEntrega;
    
    private List<ItemPedido> itens;

}
