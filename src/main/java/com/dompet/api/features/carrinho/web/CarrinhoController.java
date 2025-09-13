// CarrinhoController.java
// Endpoints que manipulam quantidades por delta (útil para ajustes incrementais via UI)
// Fornece rotas convenientes para incrementar/decrementar e alterar por delta.
package com.dompet.api.features.carrinho.web;

import com.dompet.api.features.carrinho.dto.CarrinhoDto;
import com.dompet.api.features.carrinho.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de carrinho com ajuste de quantidade por delta.
 * Útil para incrementos/decrementos transacionais com validação de estoque.
 * @deprecated Use os endpoints de {@link CartController} em /cart. Este conjunto será removido após período de transição.
 */
@RestController
@Deprecated // Preferir /cart endpoints; será removido futuramente.
@RequestMapping("/carrinho")
@Tag(name = "Carrinho", description = "Operações do carrinho")
@Validated
public class CarrinhoController {

    private final CarrinhoService service;

    public CarrinhoController(CarrinhoService service) {
        this.service = service;
    }

        /** Delta livre (positivo ou negativo). */
        @PatchMapping("/{carrinhoId}/itens/{produtoId}")
    @Operation(summary = "Alterar quantidade por delta", description = "delta positivo incrementa, negativo decrementa",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "Carrinho atualizado",
            content = @Content(schema = @Schema(implementation = CarrinhoDto.class),
                examples = @ExampleObject(value = "{\n  \"id\":1,\n  \"itens\":[{\n    \"produtoId\":1,\n    \"nomeProduto\":\"Ração X\",\n    \"unitPrice\":199.9,\n    \"quantity\":3,\n    \"lineTotal\":599.7\n  }],\n  \"total\":599.7\n}")))
    @ApiResponse(responseCode = "400", description = "Delta inválido")
    @ApiResponse(responseCode = "404", description = "Carrinho/Produto não encontrado")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    public CarrinhoDto alterarQuantidade(
            @PathVariable Long carrinhoId,
            @PathVariable Long produtoId,
            @RequestParam int delta
    ) {
        return service.alterarQuantidade(carrinhoId, produtoId, delta);
    }

        /** Açúcar sintático para incrementar (by >= 1). */
        @PostMapping("/{carrinhoId}/itens/{produtoId}/incrementar")
    @Operation(summary = "Incrementar quantidade",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "Carrinho atualizado")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    public CarrinhoDto incrementar(
            @PathVariable Long carrinhoId,
            @PathVariable Long produtoId,
            @RequestParam(name = "by", defaultValue = "1") @Min(1) @Max(1000) int by
    ) {
        return service.alterarQuantidade(carrinhoId, produtoId, by);
    }

        /** Açúcar sintático para decrementar (by >= 1). */
        @PostMapping("/{carrinhoId}/itens/{produtoId}/decrementar")
    @Operation(summary = "Decrementar quantidade",
            security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "Carrinho atualizado")
    @ApiResponse(responseCode = "400", description = "Delta inválido")
    public CarrinhoDto decrementar(
            @PathVariable Long carrinhoId,
            @PathVariable Long produtoId,
            @RequestParam(name = "by", defaultValue = "1") @Min(1) @Max(1000) int by
    ) {
        return service.alterarQuantidade(carrinhoId, produtoId, -by);
    }
}
