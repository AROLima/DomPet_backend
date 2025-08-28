package com.dompet.api.features.pedidos.domain;
import java.util.List;

import com.dompet.api.shared.endereco.Endereco;
import com.dompet.api.features.pedidos.domain.ItemPedido;
import com.dompet.api.features.pedidos.domain.StatusPedido;
import com.dompet.api.features.usuarios.domain.Usuarios;
import com.dompet.api.features.pedidos.dto.PedidosDto;
import com.dompet.api.features.pedidos.dto.PedidosDto;

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
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) 
    private Usuarios usuario;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Embedded
    private Endereco enderecoEntrega;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL) 
    private List<ItemPedido> itens;

    private Boolean ativo = true;



    public Pedidos(PedidosDto data) {
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.enderecoEntrega = data.enderecoEntrega();
        this.itens = data.itens();

    }




    public void atualizarInformacoes(PedidosDto dados) {
        if (dados.status() != null) {
            this.status = dados.status();
        }
        if (dados.enderecoEntrega() != null) {
            this.enderecoEntrega = dados.enderecoEntrega();
        }
        if (dados.itens() != null) {
            this.itens = dados.itens();
        }
    }



//exclusão lógica
public void excluir() {
    this.ativo = false;
}

}
