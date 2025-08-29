package com.dompet.api.features.produtos.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;

import com.dompet.api.features.produtos.domain.Categorias;
import com.dompet.api.features.produtos.dto.ProdutosCreateDto;
import com.dompet.api.features.produtos.dto.ProdutosReadDto;
import com.dompet.api.features.produtos.dto.ProdutosUpdateDto;
import com.dompet.api.features.produtos.service.ProdutosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller fino para Produtos.
 * - Apenas delega para ProdutosService e define as rotas/contratos HTTP.
 * - Retorna DTOs de leitura para não vazar a entidade JPA.
 */
@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Operações com produtos")
public class ProdutosController {

    private final ProdutosService service;

    public ProdutosController(ProdutosService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar um produto", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<ProdutosReadDto> cadastrarProduto(@RequestBody @Valid ProdutosCreateDto dados) {
        var salvo = service.create(dados);
        return ResponseEntity.ok(salvo);
    }

    // READ - listagem com filtros opcionais por categoria e nome
    @GetMapping
    @Operation(summary = "Listar produtos (com filtros opcionais)")
    public List<ProdutosReadDto> listarProdutos(
            @RequestParam(required = false) Categorias categoria,
            @RequestParam(required = false) String nome
    ) {
        return service.list(categoria, nome);
    }

    // READ - paginado com ativo=true preservando compatibilidade em nova rota
    @GetMapping("/search")
    @Operation(summary = "Listar produtos paginado (ativo=true) com filtros opcionais de nome e categoria")
    public Page<ProdutosReadDto> listarProdutosPaginado(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Categorias categoria,
            Pageable pageable
    ) {
        return service.search(nome, categoria, pageable);
    }

    // READ - por ID (aceita só dígitos para evitar conflito com outras rotas)
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutosReadDto> buscarProdutoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE (parcial, baseado no método atualizarInformacoes da entidade)
    @PutMapping("/{id:\\d+}")
    @Transactional
    @Operation(summary = "Atualizar um produto", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<Void> atualizarProduto(@PathVariable Long id, @RequestBody @Valid ProdutosUpdateDto dados) {
        service.update(id, dados);
        return ResponseEntity.noContent().build();
    }

    // DELETE lógico (ativo = false)
    @DeleteMapping("/{id:\\d+}")
    @Transactional
    @Operation(summary = "Excluir (lógico) um produto", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Utilitário: lista os valores possíveis do enum (útil para front)
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias")
    public Categorias[] listarCategorias() {
        return Categorias.values();
    }
}
