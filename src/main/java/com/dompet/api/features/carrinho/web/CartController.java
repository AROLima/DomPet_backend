// CartController.java
// Endpoints do carrinho (usuário autenticado) com operações de adição/remoção/atualização.
// Usa DTOs específicos para evitar expor entidades e permite respostas com subtotal/total.
package com.dompet.api.features.carrinho.web;

import com.dompet.api.features.carrinho.dto.*;
import com.dompet.api.features.carrinho.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de carrinho associados ao usuário autenticado (via Authentication).
 * Operações CRUD clássicas de itens e limpeza.
 */
@RestController
@RequestMapping("/cart")
@Tag(name = "Carrinho", description = "Operações de carrinho do usuário")
public class CartController {
    private final CarrinhoService service;
    public CartController(CarrinhoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Obter carrinho", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "Carrinho atual",
        content = @Content(schema = @Schema(implementation = CartResponseDto.class),
            examples = @ExampleObject(value = "{\n  \"id\":1,\n  \"itens\":[{\n    \"itemId\":10,\n    \"produtoId\":1,\n    \"nomeProduto\":\"Ração X\",\n    \"unitPrice\":199.9,\n    \"quantity\":2,\n    \"lineTotal\":399.8\n  }],\n  \"total\":399.8\n}")))
    public CartResponseDto getCart(Authentication auth) {
        return service.getCart(auth.getName());
    }

    @PostMapping("/items")
    @Operation(summary = "Adicionar item", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "Carrinho atualizado",
        content = @Content(schema = @Schema(implementation = CartResponseDto.class)))
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    public CartResponseDto addItem(Authentication auth, @RequestBody @Valid CartItemAddDto dto) {
        return service.addItem(auth.getName(), dto.produtoId(), dto.quantidade());
    }

    @PatchMapping("/items/{itemId}")
    @Operation(summary = "Atualizar item", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "Carrinho atualizado",
        content = @Content(schema = @Schema(implementation = CartResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Item não encontrado")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    public CartResponseDto updateItem(Authentication auth, @PathVariable Long itemId, @RequestBody @Valid CartItemUpdateDto dto) {
        return service.updateItem(auth.getName(), itemId, dto.quantidade());
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remover item", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "204", description = "Removido")
    public ResponseEntity<Void> removeItem(Authentication auth, @PathVariable Long itemId) {
        service.removeItem(auth.getName(), itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Limpar carrinho", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "204", description = "Esvaziado")
    public ResponseEntity<Void> clear(Authentication auth) {
        service.clear(auth.getName());
        return ResponseEntity.noContent().build();
    }
}
