// ProdutosController.java
// Controller de produtos que expõe rotas REST (CRUD + buscas) delegando para ProdutosService.
// Mantém contratos claros via DTOs de entrada/saída e anotações OpenAPI.
package com.dompet.api.features.produtos.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;

import com.dompet.api.features.produtos.domain.Categorias;
import com.dompet.api.features.produtos.dto.ProdutosCreateDto;
import com.dompet.api.features.produtos.dto.ProdutosReadDto;
import com.dompet.api.features.produtos.dto.ProdutosUpdateDto;
import com.dompet.api.features.produtos.service.ProdutosService;
import com.dompet.api.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.headers.Header;
import com.dompet.api.features.produtos.dto.ProdutoSuggestionDto;

/**
 * Controller fino para Produtos.
 * - Apenas delega para ProdutosService e define as rotas/contratos HTTP.
 * - Retorna DTOs de leitura para não vazar a entidade JPA.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = {"ETag", "Location", "X-API-Version"})
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Operações com produtos")
public class ProdutosController {

    private final ProdutosService service;

    public ProdutosController(ProdutosService service) {
        this.service = service;
    }

    private String computeEtag(ProdutosReadDto dto) {
        return '"' + Integer.toHexString(java.util.Objects.hash(dto.id(), dto.nome(), dto.preco(), dto.estoque(), dto.imagemUrl())) + '"';
    }

    private String computeEtagForList(List<ProdutosReadDto> list) {
        var payload = list.stream()
            .map(d -> java.util.Arrays.asList(d.id(), d.nome(), d.preco(), d.estoque(), d.imagemUrl(), d.categoria(), d.ativo(), d.sku()))
            .toList();
        return '"' + Integer.toHexString(java.util.Objects.hash(list.size(), payload)) + '"';
    }

    private String computeEtagForPage(Page<ProdutosReadDto> page) {
        var payload = page.getContent().stream()
            .map(d -> java.util.Arrays.asList(d.id(), d.nome(), d.preco(), d.estoque(), d.imagemUrl(), d.categoria(), d.ativo(), d.sku()))
            .toList();
        int h = java.util.Objects.hash(
            page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast(), payload
        );
        return '"' + Integer.toHexString(h) + '"';
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
    @ApiResponse(responseCode = "304", description = "Not Modified (If-None-Match igual ao ETag)")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProdutosReadDto.class))),
        headers = { @Header(name = "ETag", description = "Entity tag para conditional GETs", schema = @Schema(type = "string")) })
    public ResponseEntity<List<ProdutosReadDto>> listarProdutos(
            @RequestParam(required = false) Categorias categoria,
            @RequestParam(required = false) String nome,
            @RequestHeader(value = "If-None-Match", required = false) String inm
    ) {
        var list = service.list(categoria, nome);
        var etag = computeEtagForList(list);
        if (inm != null && inm.equals(etag)) {
            return ResponseEntity.status(304).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).body(list);
    }

    // READ - paginado com ativo=true preservando compatibilidade em nova rota
    @GetMapping("/search")
    @Operation(summary = "Listar produtos paginado (ativo=true) com filtros opcionais de nome e categoria")
    @ApiResponse(responseCode = "200", description = "OK",
        headers = { @Header(name = "ETag", description = "Entity tag para conditional GETs", schema = @Schema(type = "string")) },
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\n  \"content\":[{\n    \"id\":1,\n    \"nome\":\"Ração X\",\n    \"descricao\":\"Sabor frango\",\n    \"preco\":199.9,\n    \"estoque\":10,\n    \"imagemUrl\":\"https://.../racao.png\",\n    \"categoria\":\"RACAO\",\n    \"ativo\":true,\n    \"sku\":\"RACAO-X-10KG\"\n  }],\n  \"totalElements\":1,\n  \"totalPages\":1,\n  \"size\":20,\n  \"number\":0,\n  \"first\":true,\n  \"last\":true\n}")))
    @ApiResponse(responseCode = "304", description = "Not Modified (If-None-Match igual ao ETag)")
    public ResponseEntity<PageResponse<ProdutosReadDto>> listarProdutosPaginado(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Categorias categoria,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @RequestHeader(value = "If-None-Match", required = false) String inm
    ) {
        var page = service.search(nome, categoria, pageable);
        var etag = computeEtagForPage(page);
        if (inm != null && inm.equals(etag)) {
            return ResponseEntity.status(304).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).body(PageResponse.from(page));
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
            var etag = computeEtag(dto);
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
    public ResponseEntity<?> atualizarProduto(
        @PathVariable Long id,
        @RequestBody @Valid ProdutosUpdateDto dados,
        @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        // Verifica pré-condição (If-Match) usando o ETag atual do recurso
        var current = service.getById(id);
        var currentEtag = computeEtag(current);
        if (ifMatch != null && !ifMatch.equals(currentEtag)) {
            // 412 Precondition Failed com corpo ProblemDetail (Spring 6)
            var pd = org.springframework.http.ProblemDetail.forStatus(412);
            pd.setTitle("Precondition Failed");
            pd.setDetail("A versão do recurso mudou. Recarregue e tente novamente.");
            return ResponseEntity.status(412).eTag(currentEtag).body(pd);
        }

        service.update(id, dados);
        var body = service.getById(id);
        var newEtag = computeEtag(body);
        return ResponseEntity.ok().eTag(newEtag).body(body);
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
    public ResponseEntity<Categorias[]> listarCategorias() {
        var cache = CacheControl.maxAge(java.time.Duration.ofHours(1)).cachePublic();
        return ResponseEntity.ok().cacheControl(cache).body(Categorias.values());
    }



    // SUGESTÕES (autocomplete leve) - adição compatível
    @GetMapping("/suggestions")
    @Operation(summary = "Sugestões de produtos (autocomplete leve)")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "[{\n  \"id\":1,\n  \"nome\":\"Ração X\",\n  \"descricao\":\"Sabor frango\",\n  \"preco\":199.9,\n  \"estoque\":10,\n  \"imagemUrl\":\"https://.../racao.png\",\n  \"categoria\":\"RACAO\",\n  \"ativo\":true,\n  \"sku\":\"RACAO-X-10KG\"\n}]")))
    public ResponseEntity<List<ProdutoSuggestionDto>> suggestions(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "limit", defaultValue = "8") int limit,
            @RequestHeader(value = "If-None-Match", required = false) String inm
    ) {
        var list = service.suggestions(q, limit);
        var etag = '"' + Integer.toHexString(java.util.Objects.hash(list.size(),
            list.stream().map(d -> java.util.Arrays.asList(d.id(), d.nome(), d.imagemUrl(), d.sku())).toList())) + '"';
        if (inm != null && inm.equals(etag)) {
            return ResponseEntity.status(304).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).body(list);
    }
}
