package com.dompet.api.features.pedidos.web;

import com.dompet.api.features.pedidos.domain.StatusPedido;
import com.dompet.api.features.pedidos.dto.CheckoutDto;
import com.dompet.api.features.pedidos.dto.PedidoResponseDto;
import com.dompet.api.features.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Endpoints de pedidos: checkout do carrinho, listar/obter pedidos do usuário,
 * e atualização de status por ADMIN.
 */
@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Operações com pedidos")
public class PedidosController {

    private final PedidoService service;
    public PedidosController(PedidoService service) { this.service = service; }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout do carrinho", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "201", description = "Criado",
        content = @Content(schema = @Schema(implementation = PedidoResponseDto.class),
            examples = @ExampleObject(value = "{\n  \"id\":123,\n  \"status\":\"AGUARDANDO_PAGAMENTO\",\n  \"enderecoEntrega\":{\n    \"rua\":\"Rua A\",\n    \"numero\":\"100\",\n    \"bairro\":\"Centro\",\n    \"cep\":\"12345-678\",\n    \"cidade\":\"São Paulo\",\n    \"complemento\":\"Apto 12\"\n  },\n  \"itens\":[{\n    \"produtoId\":1,\n    \"nome\":\"Ração X\",\n    \"precoUnitario\":199.9,\n    \"quantidade\":2,\n    \"subtotal\":399.8\n  }],\n  \"total\":399.8,\n  \"createdAt\":\"2025-08-30T12:34:56Z\"\n}")))
    @ApiResponse(responseCode = "400", description = "Carrinho vazio ou inválido")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    public ResponseEntity<PedidoResponseDto> checkout(Authentication auth, @RequestBody @Valid CheckoutDto dto) {
        var resp = service.checkout(auth.getName(), dto);
        var location = java.net.URI.create("/pedidos/" + resp.id());
        return ResponseEntity.created(location).body(resp);
    }

    // Alias compatível: POST /pedidos -> checkout
    @PostMapping
    @Operation(summary = "Criar pedido (alias de checkout)", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "201", description = "Criado",
        content = @Content(schema = @Schema(implementation = PedidoResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Carrinho vazio ou inválido")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    public ResponseEntity<PedidoResponseDto> criarPedido(Authentication auth, @RequestBody @Valid CheckoutDto dto) {
        var resp = service.checkout(auth.getName(), dto);
        var location = java.net.URI.create("/pedidos/" + resp.id());
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter pedido por ID (dono ou ADMIN)", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(schema = @Schema(implementation = PedidoResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not Found")
    public PedidoResponseDto getById(Authentication auth, @PathVariable Long id) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return service.getById(auth.getName(), id, isAdmin);
    }

    @GetMapping
    @Operation(summary = "Listar meus pedidos", security = { @SecurityRequirement(name = "bearerAuth") })
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = "{\n  \"content\": [{\n    \"id\": 123,\n    \"status\": \"AGUARDANDO_PAGAMENTO\",\n    \"enderecoEntrega\": {\n      \"rua\": \"Rua A\",\n      \"numero\": \"100\",\n      \"bairro\": \"Centro\",\n      \"cep\": \"12345-678\",\n      \"cidade\": \"São Paulo\",\n      \"complemento\": \"Apto 12\"\n    },\n    \"itens\": [{\n      \"produtoId\": 1,\n      \"nome\": \"Ração X\",\n      \"precoUnitario\": 199.9,\n      \"quantidade\": 2,\n      \"subtotal\": 399.8\n    }],\n    \"total\": 399.8,\n    \"createdAt\": \"2025-08-30T12:34:56Z\"\n  }],\n  \"totalElements\": 1,\n  \"totalPages\": 1,\n  \"size\": 20,\n  \"number\": 0\n}")))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public Page<PedidoResponseDto> listMine(Authentication auth, Pageable p) {
        return service.listMine(auth.getName(), p);
    }

    record UpdateStatusDto(String status) {}

    /** Atualização de status restrita a ADMIN (verificada na security). */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status (ADMIN)", security = { @SecurityRequirement(name = "bearerAuth") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UpdateStatusDto.class),
            examples = @ExampleObject(value = "{\n  \"status\": \"PAGO\"\n}")))
    @ApiResponse(responseCode = "204", description = "Atualizado")
    @ApiResponse(responseCode = "400", description = "Transição inválida")
    @ApiResponse(responseCode = "404", description = "Not Found")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody @Valid UpdateStatusDto body) {
        StatusPedido novo = StatusPedido.valueOf(body.status());
        service.updateStatusAsAdmin(id, novo);
        return ResponseEntity.noContent().build();
    }
}
