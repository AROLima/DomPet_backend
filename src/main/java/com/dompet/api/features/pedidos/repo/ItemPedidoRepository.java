package com.dompet.api.features.pedidos.repo;

import com.dompet.api.features.pedidos.domain.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

/** CRUD básico de itens do pedido. */
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {}
