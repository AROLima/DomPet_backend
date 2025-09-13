package com.dompet.api.features.pedidos.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.dompet.api.features.pedidos.dto.CheckoutRequestDto;
import com.dompet.api.features.pedidos.dto.PedidoDto;
import com.dompet.api.features.pedidos.service.PedidosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/pedidos")
@Validated
public class PedidosController {

  private final PedidosService pedidosService;

  public PedidosController(PedidosService pedidosService) {
    this.pedidosService = pedidosService;
  }

  @PostMapping("/checkout")
  @Operation(summary = "Checkout (criar pedido a partir do carrinho)", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "200", description = "Pedido criado")
  public ResponseEntity<PedidoDto> checkout(Authentication auth, @RequestBody Object raw) {
    // Aceita dois formatos:
    // 1) { "rua":..., "numero":..., ... }
    // 2) { "enderecoEntrega": { "rua":..., ... }, "observacoes": "...", "metodoPagamento": "..." }
    CheckoutRequestDto body;
    if (raw instanceof java.util.Map<?,?> map) {
      Object maybeNested = map.get("enderecoEntrega");
      String observacoes = (String) map.getOrDefault("observacoes", null);
      String metodoPagamento = (String) map.getOrDefault("metodoPagamento", null);
      if (maybeNested instanceof java.util.Map<?,?> nested) {
        body = new CheckoutRequestDto(
          str(nested.get("rua")),
          str(nested.get("numero")),
          str(nested.get("bairro")),
          str(nested.get("cep")),
          str(nested.get("cidade")),
          observacoes,
          metodoPagamento
        );
      } else {
        body = new CheckoutRequestDto(
          str(map.get("rua")),
          str(map.get("numero")),
          str(map.get("bairro")),
          str(map.get("cep")),
          str(map.get("cidade")),
          observacoes,
          metodoPagamento
        );
      }
    } else {
      return ResponseEntity.badRequest().build();
    }
    var dto = pedidosService.checkout(auth.getName(), body);
    return ResponseEntity.ok(dto);
  }

  private static String str(Object o) { return o == null ? null : o.toString(); }

  @GetMapping
  @Operation(summary = "Listar pedidos do usuário autenticado", security = { @SecurityRequirement(name = "bearerAuth") })
  public Page<PedidoDto> list(Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return pedidosService.listForUser(auth.getName(), PageRequest.of(page, size));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obter pedido por id (do usuário)", security = { @SecurityRequirement(name = "bearerAuth") })
  public PedidoDto getOne(Authentication auth, @PathVariable Long id) {
    return pedidosService.getOne(auth.getName(), id);
  }

  public record UpdateStatusRequest(String status) {}

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Atualizar status do pedido (ADMIN)", security = { @SecurityRequirement(name = "bearerAuth") })
  public PedidoDto updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest body) {
    if (body == null || body.status() == null) throw new IllegalArgumentException("status requerido");
    var st = com.dompet.api.features.pedidos.domain.PedidoStatus.valueOf(body.status());
    return pedidosService.updateStatus(id, st);
  }
}
