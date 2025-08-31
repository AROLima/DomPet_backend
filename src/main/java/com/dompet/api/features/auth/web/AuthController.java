// com/dompet/api/features/auth/web/AuthController.java
// Endpoints para registrar usuário e fazer login (gerando JWT)
package com.dompet.api.features.auth.web;

import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.dompet.api.features.auth.dto.*;
import com.dompet.api.features.auth.token.TokenService;
import com.dompet.api.features.usuarios.domain.Usuarios;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;
import com.dompet.api.features.usuarios.domain.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticação e registro")
public class AuthController {

  private final UsuariosRepository usuariosRepo;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authManager;
  private final TokenService tokenService;

  public AuthController(UsuariosRepository usuariosRepo,
                        PasswordEncoder encoder,
                        AuthenticationManager authManager,
                        TokenService tokenService) {
  // Injeção de dependências: repositório, encoder de senha, auth manager do Spring e serviço de tokens
    this.usuariosRepo = usuariosRepo;
    this.encoder = encoder;
    this.authManager = authManager;
    this.tokenService = tokenService;
  }

  @PostMapping("/register")
  @Operation(summary = "Registro de usuário")
  @ApiResponse(responseCode = "201", description = "Usuário registrado",
      content = @Content(schema = @Schema(implementation = com.dompet.api.features.auth.dto.AuthResponseDto.class),
          examples = @ExampleObject(value = "{\n  \"token\": \"<jwt>\",\n  \"expiresIn\": 3600\n}")))
  @Transactional
  public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid AuthRegisterDto dto) {
  // Bloqueia cadastro com email duplicado
    if (usuariosRepo.existsByEmail(dto.email())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
  // Cria o usuário com senha criptografada e role padrão USER
    var u = new Usuarios();
    u.setNome(dto.nome());
    u.setEmail(dto.email());
    u.setSenha(encoder.encode(dto.senha()));
    u.setRole(Role.USER);
    u.setAtivo(true);
    usuariosRepo.save(u);

    // login automático depois do register (opcional)
  var token = loginInternal(dto.email(), dto.senha());
  return ResponseEntity.status(HttpStatus.CREATED)
      .header("X-API-Version", "1")
      .body(new AuthResponseDto(token, tokenService.getExpirationMs()));
  }

  @PostMapping("/login")
  @Operation(summary = "Login e emissão de JWT")
  @ApiResponse(responseCode = "200", description = "Login ok",
      content = @Content(schema = @Schema(implementation = com.dompet.api.features.auth.dto.AuthResponseDto.class),
          examples = @ExampleObject(value = "{\n  \"token\": \"<jwt>\",\n  \"expiresIn\": 3600\n}")))
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthLoginDto dto) {
  // Autentica usando AuthenticationManager (trigga UserDetailsService + PasswordEncoder)
  var token = loginInternal(dto.email(), dto.senha());
  return ResponseEntity.ok()
      .header("X-API-Version", "1")
      .body(new AuthResponseDto(token, tokenService.getExpirationMs()));
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout (cliente deve descartar o token)", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "204", description = "Logout efetuado")
  public ResponseEntity<Void> logout() {
    // JWT é stateless: apenas retorna 204. Se um dia usar cookie HttpOnly, zere-o aqui.
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/logout-all")
  @Transactional
  @Operation(summary = "Logout em todos os dispositivos (incrementa tokenVersion)", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "204", description = "Tokens anteriores invalidados")
  public ResponseEntity<Void> logoutAll(org.springframework.security.core.Authentication auth) {
    var email = auth.getName();
    var user = usuariosRepo.findByEmail(email).orElse(null);
    if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    user.bumpTokenVersion();
    // JPA dirty checking salva ao sair do método (@Transactional)
    return ResponseEntity.noContent().build();
  }

  /**
   * Refresh token simples: reemite um JWT para o usuário autenticado.
   * Compatível e opcional: se quiser adotar refresh-token separado depois, este endpoint pode evoluir.
   */
  @PostMapping("/refresh")
  @Transactional(readOnly = true)
  @Operation(summary = "Refresh de JWT (reemitir token para o usuário atual)", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "200", description = "Token reemitido",
      content = @Content(schema = @Schema(implementation = com.dompet.api.features.auth.dto.AuthResponseDto.class),
          examples = @ExampleObject(value = "{\n  \"token\": \"<jwt>\",\n  \"expiresIn\": 3600\n}")))
  public ResponseEntity<AuthResponseDto> refresh(org.springframework.security.core.Authentication auth) {
    if (auth == null || auth.getName() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    var user = usuariosRepo.findByEmail(auth.getName()).orElse(null);
    if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // Reemite token baseado nas roles atuais do usuário
    var principal = org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getSenha())
        .authorities(user.getRole().getAuthorities())
        .build();
  var token = tokenService.generate(principal);
  return ResponseEntity.ok()
    .header("X-API-Version", "1")
    .body(new AuthResponseDto(token, tokenService.getExpirationMs()));
  }

  private String loginInternal(String email, String senha) {
  // Cria um token de autenticação com email/senha
    var authToken = new UsernamePasswordAuthenticationToken(email, senha);
  // Dispara autenticação: verifica usuário e senha
  var auth = authManager.authenticate(authToken); // dispara UserDetailsService + encoder
    UserDetails principal = (UserDetails) auth.getPrincipal();
  // Retorna um JWT assinado contendo o email e as roles
    return tokenService.generate(principal);
  }

  // Mantido para compatibilidade histórica caso alguém use; não utilizado pelas respostas atuais
  private long getExpiresInSeconds() { return Math.max(1L, tokenService.getExpirationMs() / 1000L); }
}
