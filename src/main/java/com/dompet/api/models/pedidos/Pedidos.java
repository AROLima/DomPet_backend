package com.dompet.api.models.pedidos;
import java.util.List;

import com.dompet.api.models.endereco.Endereco;
import com.dompet.api.models.itempedido.ItemPedido;
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
public void setAtivo(boolean ativo) {
    this.ativo = ativo;

}

}
