// UsuariosController.java
// Controller didático: endpoint para obter informações do usuário autenticado.
// Explica fluxo: autenticação via SecurityContext (Authentication), busca usuário
// por email e mapeamento para DTO para evitar expor senha.
package com.dompet.api.features.usuarios.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;
import com.dompet.api.features.usuarios.dto.UsuariosDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller simples que expõe informações do usuário autenticado.
 * Endpoint: GET /usuarios/me
 */
@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
  private final UsuariosRepository repo;
  public UsuariosController(UsuariosRepository repo) { this.repo = repo; }

  @GetMapping("/me")
  @Operation(summary = "Dados do usuário autenticado", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "401", description = "Unauthorized")
  public ResponseEntity<UsuariosDto> me(Authentication auth) {
    // Verifica se existe autenticação no contexto
    if (auth == null || auth.getName() == null) return ResponseEntity.status(401).build();
  var opt = repo.findByEmailIgnoreCase(auth.getName());
    if (opt.isEmpty()) return ResponseEntity.status(401).build();
    var u = opt.get();
    // Monta DTO para expor campos públicos do usuário (não enviamos senha)
    var dto = new UsuariosDto(
      u.getNome(),
      u.getEmail(),
      null, // senha omitida
      u.getEndereco(),
      null, // campo sensível omitido
      u.getRole(),
      u.getAtivo()
    );
    return ResponseEntity.ok(dto);
  }
}
