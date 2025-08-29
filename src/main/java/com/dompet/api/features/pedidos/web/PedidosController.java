package com.dompet.api.features.pedidos.web;

import com.dompet.api.features.pedidos.domain.StatusPedido;
import com.dompet.api.features.pedidos.dto.CheckoutDto;
import com.dompet.api.features.pedidos.dto.PedidoResponseDto;
import com.dompet.api.features.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PedidoResponseDto> checkout(Authentication auth, @RequestBody CheckoutDto dto) {
        var resp = service.checkout(auth.getName(), dto);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter pedido por ID (dono ou ADMIN)", security = { @SecurityRequirement(name = "bearerAuth") })
    public PedidoResponseDto getById(Authentication auth, @PathVariable Long id) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return service.getById(auth.getName(), id, isAdmin);
    }

    @GetMapping
    @Operation(summary = "Listar meus pedidos", security = { @SecurityRequirement(name = "bearerAuth") })
    public Page<PedidoResponseDto> listMine(Authentication auth, Pageable p) {
        return service.listMine(auth.getName(), p);
    }

    record UpdateStatusDto(String status) {}

    /** Atualização de status restrita a ADMIN (verificada na security). */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status (ADMIN)", security = { @SecurityRequirement(name = "bearerAuth") })
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusDto body) {
        StatusPedido novo = StatusPedido.valueOf(body.status());
        service.updateStatusAsAdmin(id, novo);
        return ResponseEntity.noContent().build();
    }
}
