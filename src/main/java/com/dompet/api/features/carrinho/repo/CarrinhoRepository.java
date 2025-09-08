package com.dompet.api.features.carrinho.repo;

import com.dompet.api.features.carrinho.domain.Carrinho;
import com.dompet.api.features.carrinho.domain.CartStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    /** Encontra carrinho pelo e-mail do usuário e status. */
    Optional<Carrinho> findByUsuarioEmailAndStatus(String email, CartStatus status);

    /**
     * Variante resiliente: retorna apenas o mais recente quando houver duplicados.
     * Evita IncorrectResultSizeDataAccessException caso existam múltiplos carrinhos ABERTOs.
     */
    Optional<Carrinho> findFirstByUsuarioEmailAndStatusOrderByUpdatedAtDesc(String email, CartStatus status);

    /** Busca carrinho por ID já carregando itens e produtos (evita N+1). */
    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    Optional<Carrinho> findWithItensById(Long id);
}
