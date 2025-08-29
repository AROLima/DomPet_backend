package com.dompet.api.features.carrinho.repo;

import com.dompet.api.features.carrinho.domain.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
	/** Localiza o item de um produto específico dentro de um carrinho. */
	Optional<ItemCarrinho> findByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);
}
