package com.dompet.api.models.produtos;

import com.dompet.api.models.categorias.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutosRepository extends JpaRepository<Produtos, Long>{
    
    // Buscar produtos por categoria
    List<Produtos> findByCategoria(Categorias categoria);
    
    // Buscar produtos por ID da categoria
    List<Produtos> findByCategoriaId(Long categoriaId);
    
    // Buscar produtos por nome da categoria
    List<Produtos> findByCategoriaNome(String nomeCategoria);
}
