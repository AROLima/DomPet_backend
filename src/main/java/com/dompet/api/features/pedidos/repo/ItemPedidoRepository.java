package com.dompet.api.features.pedidos.repo;

import com.dompet.api.features.pedidos.domain.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** CRUD básico de itens do pedido. */
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
	/** Busca um item de pedido pelo seu ID. (retorna Optional para presença/ausência) */
	Optional<ItemPedido> findById(Long id);
}
