package com.dompet.api.models.produtos;

import com.dompet.api.models.categorias.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutosRepository extends JpaRepository<Produtos, Long>{
    
    List<Produtos> findByCategoria(Categorias categoria);  // 
    // outras consultas úteis:
    List<Produtos> findByNomeContainingIgnoreCase(String nome);
}
