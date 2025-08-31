package com.dompet.api.features.carrinho.repo;

import com.dompet.api.features.carrinho.domain.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para persistência de itens do carrinho. Contém buscas úteis como
 * localizar um item por carrinho e produto (para somar quantidades em adição).
 */
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
	/** Localiza o item de um produto específico dentro de um carrinho. */
	Optional<ItemCarrinho> findByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);
}
