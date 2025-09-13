package com.dompet.api.features.pedidos.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dompet.api.features.pedidos.domain.Pedido;

public interface PedidosRepository extends JpaRepository<Pedido, Long> {
  Page<Pedido> findByUsuarioEmailOrderByCreatedAtDesc(String email, Pageable pageable);
}

