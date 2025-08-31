package com.dompet.api.features.produtos.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.headers.Header;
import org.springframework.data.domain.PageRequest;

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
    @ApiResponse(responseCode = "201", description = "Criado",
        content = @Content(schema = @Schema(implementation = ProdutosReadDto.class),
            examples = @ExampleObject(value = "{\n  \"id\":1,\n  \"nome\":\"Ração X\",\n  \"descricao\":\"Sabor frango\",\n  \"preco\":199.9,\n  \"estoque\":10,\n  \"imagemUrl\":\"https://.../racao.png\",\n  \"categoria\":\"RACAO\",\n  \"ativo\":true,\n  \"sku\":\"RACAO-X-10KG\"\n}")))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutosReadDto> cadastrarProduto(@RequestBody @Valid ProdutosCreateDto dados) {
    var salvo = service.create(dados);
    var location = java.net.URI.create("/produtos/" + salvo.id());
    return ResponseEntity.created(location).body(salvo);
    }

    // READ - listagem com filtros opcionais por categoria e nome
    @GetMapping
    @Operation(summary = "Listar produtos (com filtros opcionais)")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProdutosReadDto.class)),
            examples = @ExampleObject(value = "[{\n  \"id\":1,\n  \"nome\":\"Ração X\",\n  \"descricao\":\"Sabor frango\",\n  \"preco\":199.9,\n  \"estoque\":10,\n  \"imagemUrl\":\"https://.../racao.png\",\n  \"categoria\":\"RACAO\",\n  \"ativo\":true,\n  \"sku\":\"RACAO-X-10KG\"\n}]")))
    public List<ProdutosReadDto> listarProdutos(
            @RequestParam(required = false) Categorias categoria,
            @RequestParam(required = false) String nome
    ) {
        return service.list(categoria, nome);
    }

    // READ - paginado com ativo=true preservando compatibilidade em nova rota
    @GetMapping("/search")
    @Operation(summary = "Listar produtos paginado (ativo=true) com filtros opcionais de nome e categoria")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\n  \"content\":[{\n    \"id\":1,\n    \"nome\":\"Ração X\",\n    \"descricao\":\"Sabor frango\",\n    \"preco\":199.9,\n    \"estoque\":10,\n    \"imagemUrl\":\"https://.../racao.png\",\n    \"categoria\":\"RACAO\",\n    \"ativo\":true,\n    \"sku\":\"RACAO-X-10KG\"\n  }],\n  \"totalElements\":1,\n  \"totalPages\":1,\n  \"size\":20,\n  \"number\":0\n}")))
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
    @ApiResponse(responseCode = "200", description = "OK",
        headers = { @Header(name = "ETag", description = "Entity tag para conditional GETs", schema = @Schema(type = "string")) },
        content = @Content(
            schema = @Schema(implementation = ProdutosReadDto.class),
            examples = @ExampleObject(value = "{\n  \"id\":1,\n  \"nome\":\"Ração X\",\n  \"descricao\":\"Sabor frango\",\n  \"preco\":199.9,\n  \"estoque\":10,\n  \"imagemUrl\":\"https://.../racao.png\",\n  \"categoria\":\"RACAO\",\n  \"ativo\":true,\n  \"sku\":\"RACAO-X-10KG\"\n}")))
    @ApiResponse(responseCode = "304", description = "Not Modified (If-None-Match igual ao ETag)")
    @ApiResponse(responseCode = "404", description = "Not Found")
    public ResponseEntity<ProdutosReadDto> buscarProdutoPorId(@PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false) String inm) {
        try {
            var dto = service.getById(id);
            // ETag simples baseado nos campos principais
            var etag = '"' + Integer.toHexString(java.util.Objects.hash(dto.id(), dto.nome(), dto.preco(), dto.estoque(), dto.imagemUrl())) + '"';
            if (inm != null && inm.equals(etag)) {
                return ResponseEntity.status(304).eTag(etag).build();
            }
            return ResponseEntity.ok().eTag(etag).body(dto);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE (parcial, baseado no método atualizarInformacoes da entidade)
    @PutMapping("/{id:\\d+}")
    @Transactional
    @Operation(summary = "Atualizar um produto", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(schema = @Schema(implementation = ProdutosReadDto.class)))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutosReadDto> atualizarProduto(@PathVariable Long id, @RequestBody @Valid ProdutosUpdateDto dados) {
        service.update(id, dados);
        var body = service.getById(id);
        return ResponseEntity.ok(body);
    }

    // DELETE lógico (ativo = false)
    @DeleteMapping("/{id:\\d+}")
    @Transactional
    @Operation(summary = "Excluir (lógico) um produto", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "204", description = "Excluído")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Utilitário: lista os valores possíveis do enum (útil para front)
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias")
    @ApiResponse(responseCode = "200", description = "OK")
    public Categorias[] listarCategorias() {
        return Categorias.values();
    }

    // SUGESTÕES (autocomplete leve) - adição compatível
    @GetMapping("/suggestions")
    @Operation(summary = "Sugestões de produtos (autocomplete leve)")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "[{\n  \"id\":1,\n  \"nome\":\"Ração X\",\n  \"descricao\":\"Sabor frango\",\n  \"preco\":199.9,\n  \"estoque\":10,\n  \"imagemUrl\":\"https://.../racao.png\",\n  \"categoria\":\"RACAO\",\n  \"ativo\":true,\n  \"sku\":\"RACAO-X-10KG\"\n}]")))
    public List<ProdutosReadDto> suggestions(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "limit", defaultValue = "8") int limit
    ) {
        var pageable = PageRequest.of(0, Math.max(1, Math.min(limit, 50)));
        return service.search(q, null, pageable).getContent();
    }
}
