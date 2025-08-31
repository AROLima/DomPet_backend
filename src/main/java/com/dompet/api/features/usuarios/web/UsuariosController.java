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
    if (auth == null || auth.getName() == null) return ResponseEntity.status(401).build();
    var opt = repo.findByEmail(auth.getName());
    if (opt.isEmpty()) return ResponseEntity.status(401).build();
    var u = opt.get();
    var dto = new UsuariosDto(
      u.getNome(),
      u.getEmail(),
      null,
      u.getEndereco(),
      null,
      u.getRole(),
      u.getAtivo()
    );
    return ResponseEntity.ok(dto);
  }
}
