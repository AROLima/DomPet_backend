package com.dompet.api.features.pedidos.repo;

import com.dompet.api.features.pedidos.domain.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {}
