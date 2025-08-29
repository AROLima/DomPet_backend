package com.dompet.api.features.carrinho.repo;

import com.dompet.api.features.carrinho.domain.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {}
