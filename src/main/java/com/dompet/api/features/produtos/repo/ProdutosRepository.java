package com.dompet.api.features.produtos.repo;

import com.dompet.api.features.produtos.domain.Categorias;
import com.dompet.api.features.produtos.domain.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutosRepository extends JpaRepository<Produtos, Long>{
    
    List<Produtos> findByCategoria(Categorias categoria);  // 
    // outras consultas úteis:
    List<Produtos> findByNomeContainingIgnoreCase(String nome);
}
