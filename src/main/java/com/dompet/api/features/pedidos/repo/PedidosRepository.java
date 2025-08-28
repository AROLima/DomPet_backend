package com.dompet.api.features.pedidos.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dompet.api.features.pedidos.domain.Pedidos;


@Repository
public interface PedidosRepository extends JpaRepository<Pedidos, Long> {
    
}
