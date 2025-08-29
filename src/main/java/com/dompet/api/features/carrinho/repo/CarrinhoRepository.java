package com.dompet.api.features.carrinho.repo;

import com.dompet.api.features.carrinho.domain.Carrinho;
import com.dompet.api.features.carrinho.domain.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioEmailAndStatus(String email, CartStatus status);
}
