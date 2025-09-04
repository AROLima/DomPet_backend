package com.dompet.api.features.pedidos.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dompet.api.features.pedidos.domain.Pedidos;

public interface PedidosRepository extends JpaRepository<Pedidos, Long> {
	/** Lista pedidos de um usuário pelo e-mail com paginação. */
	Page<Pedidos> findByUsuarioEmail(String email, Pageable pageable);
	// Nota: findAll(Pageable) já é herdado de JpaRepository.
}
