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

    public Pedidos(PedidosDto data) {
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.enderecoEntrega = data.enderecoEntrega(); 
        // Assuming you will set the address later
        // You might want to set the usuario and itens based on your application logic
    }
}
