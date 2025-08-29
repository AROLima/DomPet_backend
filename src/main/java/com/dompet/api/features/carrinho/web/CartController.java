package com.dompet.api.features.carrinho.web;

import com.dompet.api.features.carrinho.dto.*;
import com.dompet.api.features.carrinho.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name = "Carrinho", description = "Operações de carrinho do usuário")
public class CartController {
    private final CarrinhoService service;
    public CartController(CarrinhoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Obter carrinho", security = { @SecurityRequirement(name = "bearerAuth") })
    public CartResponseDto getCart(Authentication auth) {
        return service.getCart(auth.getName());
    }

    @PostMapping("/items")
    @Operation(summary = "Adicionar item", security = { @SecurityRequirement(name = "bearerAuth") })
    public CartResponseDto addItem(Authentication auth, @RequestBody @Valid CartItemAddDto dto) {
        return service.addItem(auth.getName(), dto.produtoId(), dto.quantidade());
    }

    @PatchMapping("/items/{itemId}")
    @Operation(summary = "Atualizar item", security = { @SecurityRequirement(name = "bearerAuth") })
    public CartResponseDto updateItem(Authentication auth, @PathVariable Long itemId, @RequestBody @Valid CartItemUpdateDto dto) {
        return service.updateItem(auth.getName(), itemId, dto.quantidade());
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remover item", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<Void> removeItem(Authentication auth, @PathVariable Long itemId) {
        service.removeItem(auth.getName(), itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Limpar carrinho", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<Void> clear(Authentication auth) {
        service.clear(auth.getName());
        return ResponseEntity.noContent().build();
    }
}
