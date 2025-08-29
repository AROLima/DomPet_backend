package com.dompet.api.features.produtos.service;

import com.dompet.api.features.produtos.domain.Produtos;
import com.dompet.api.features.produtos.dto.*;
import com.dompet.api.features.produtos.repo.ProdutosRepository;
import com.dompet.api.features.produtos.domain.Categorias;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Regras de negócio e mapeamento de Produtos.
 * Controla DTOs de entrada/saída e evita expor entidades diretamente.
 */
@Service
public class ProdutosService {
    private final ProdutosRepository repo;

    public ProdutosService(ProdutosRepository repo) {
        this.repo = repo;
    }

    // Mapeamentos entre entidade e DTOs
    private ProdutosReadDto toDto(Produtos p) {
        return new ProdutosReadDto(p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getEstoque(), p.getImagemUrl(), p.getCategoria(), p.getAtivo());
    }

    private Produtos fromCreate(ProdutosCreateDto dto) {
        return new Produtos(new ProdutosDto(dto.nome(), dto.descricao(), dto.preco(), dto.estoque(), dto.imagemUrl(), dto.ativo(), dto.categoria()));
    }

    /**
     * Lista não-paginada para manter compatibilidade com GET /produtos.
     * Se categoria for informada, filtra por categoria; se nome for informado, filtra por nome; senão traz todos.
     */
    @Transactional(readOnly = true)
    public List<ProdutosReadDto> list(Categorias categoria, String nome) {
        if (categoria != null) {
            return repo.findByCategoria(categoria).stream().map(this::toDto).collect(Collectors.toList());
        }
        if (nome != null) {
            return repo.findByNomeContainingIgnoreCase(nome).stream().map(this::toDto).collect(Collectors.toList());
        }
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Cria um produto e retorna DTO de leitura. */
    @Transactional
    public ProdutosReadDto create(ProdutosCreateDto dto) {
        var saved = repo.save(fromCreate(dto));
        return toDto(saved);
    }

    /**
     * Busca paginada com ativo=true. Aceita filtros opcionais de nome e categoria.
     */
    @Transactional(readOnly = true)
    public Page<ProdutosReadDto> search(String nome, Categorias categoria, Pageable pageable) {
        if (nome != null && !nome.isBlank() && categoria != null) {
            return repo.findByNomeContainingIgnoreCaseAndCategoriaAndAtivoTrue(nome, categoria, pageable).map(this::toDto);
        }
        if (nome != null && !nome.isBlank()) {
            return repo.findByNomeContainingIgnoreCaseAndAtivoTrue(nome, pageable).map(this::toDto);
        }
        if (categoria != null) {
            return repo.findByCategoriaAndAtivoTrue(categoria, pageable).map(this::toDto);
        }
        return repo.findAllByAtivoTrue(pageable).map(this::toDto);
    }

    /** Busca por ID; lança 404 (EntityNotFoundException) quando não encontrado. */
    @Transactional(readOnly = true)
    public ProdutosReadDto getById(Long id) {
        var p = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        return toDto(p);
    }

    /** Atualiza campos permitidos via DTO; JPA faz dirty checking. */
    @Transactional
    public void update(Long id, ProdutosUpdateDto dto) {
        var p = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        var base = new ProdutosDto(dto.nome(), dto.descricao(), dto.preco(), dto.estoque(), dto.imagemUrl(), dto.ativo(), dto.categoria());
        p.atualizarInformacoes(base);
    }

    /** Exclusão lógica: marca ativo=false. */
    @Transactional
    public void delete(Long id) {
        var p = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        p.excluir();
    }
}
