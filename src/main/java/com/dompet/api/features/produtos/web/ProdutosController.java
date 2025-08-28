package com.dompet.api.features.produtos.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // use this one (Spring)
import org.springframework.web.bind.annotation.*;

import com.dompet.api.features.produtos.domain.Categorias;
import com.dompet.api.features.produtos.domain.Produtos;
import com.dompet.api.features.produtos.dto.ProdutosDto;
import com.dompet.api.features.produtos.repo.ProdutosRepository;

@RestController
@RequestMapping("/produtos")
public class ProdutosController {

    @Autowired
    private ProdutosRepository repository;

    // CREATE
    @PostMapping
    @Transactional
    public ResponseEntity<Produtos> cadastrarProduto(@RequestBody ProdutosDto dados) {
        Produtos salvo = repository.save(new Produtos(dados));
        return ResponseEntity.ok(salvo);
    }

    // READ - listagem com filtros opcionais por categoria e nome
    @GetMapping
    public List<Produtos> listarProdutos(
            @RequestParam(required = false) Categorias categoria,
            @RequestParam(required = false) String nome
    ) {
    // Se ambos presentes, prioriza filtro por categoria
        if (categoria != null) {
            return repository.findByCategoria(categoria);
        }
        if (nome != null) {
            return repository.findByNomeContainingIgnoreCase(nome);
        }
        return repository.findAll();
    }

    // READ - por ID (aceita só dígitos para evitar conflito com outras rotas)
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Produtos> buscarProdutoPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE (parcial, baseado no seu atualizarInformacoes do entity)
    @PutMapping("/{id:\\d+}")
    @Transactional
    public ResponseEntity<Void> atualizarProduto(@PathVariable Long id, @RequestBody ProdutosDto dados) {
    java.util.Optional<Produtos> opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

    Produtos produto = opt.get();
        produto.atualizarInformacoes(dados); // entity já trata null/0
        // como está em @Transactional e é entidade gerenciada, não precisa chamar save
        return ResponseEntity.noContent().build();
    }

    // DELETE lógico (ativo = false)
    @DeleteMapping("/{id:\\d+}")
    @Transactional
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
    java.util.Optional<Produtos> opt = repository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

    Produtos produto = opt.get();
    produto.excluir();
        return ResponseEntity.noContent().build();
    }

    // Utilitário opcional: lista os valores possíveis do enum (bom pra front)
    @GetMapping("/categorias")
    public Categorias[] listarCategorias() {
        return Categorias.values();
    }
}
