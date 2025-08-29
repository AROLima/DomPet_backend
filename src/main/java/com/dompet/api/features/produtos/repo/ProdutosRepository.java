package com.dompet.api.features.produtos.repo;

import com.dompet.api.features.produtos.domain.Categorias;
import com.dompet.api.features.produtos.domain.Produtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório de produtos com consultas por categoria/nome.
 * Paginadas trazem apenas produtos ativos (ativo=true).
 */
public interface ProdutosRepository extends JpaRepository<Produtos, Long>{

    List<Produtos> findByCategoria(Categorias categoria);
    List<Produtos> findByNomeContainingIgnoreCase(String nome);

    // Paginados (somente ativos)
    Page<Produtos> findAllByAtivoTrue(Pageable pageable);
    Page<Produtos> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);
    Page<Produtos> findByCategoriaAndAtivoTrue(Categorias categoria, Pageable pageable);
    Page<Produtos> findByNomeContainingIgnoreCaseAndCategoriaAndAtivoTrue(String nome, Categorias categoria, Pageable pageable);
}
