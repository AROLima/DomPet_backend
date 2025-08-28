// com/dompet/api/features/auth/web/AuthController.java
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UsuariosRepository usuariosRepo;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authManager;
  private final TokenService tokenService;

  public AuthController(UsuariosRepository usuariosRepo,
                        PasswordEncoder encoder,
                        AuthenticationManager authManager,
                        TokenService tokenService) {
    this.usuariosRepo = usuariosRepo;
    this.encoder = encoder;
    this.authManager = authManager;
    this.tokenService = tokenService;
  }

  @PostMapping("/register")
  @Transactional
  public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid AuthRegisterDto dto) {
    if (usuariosRepo.existsByEmail(dto.email())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    var u = new Usuarios();
    u.setNome(dto.nome());
    u.setEmail(dto.email());
    u.setSenha(encoder.encode(dto.senha()));
    u.setRole(Role.USER);
    u.setAtivo(true);
    usuariosRepo.save(u);

    // login automático depois do register (opcional)
    var token = loginInternal(dto.email(), dto.senha());
    return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDto(token));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthLoginDto dto) {
    var token = loginInternal(dto.email(), dto.senha());
    return ResponseEntity.ok(new AuthResponseDto(token));
  }

  private String loginInternal(String email, String senha) {
    var authToken = new UsernamePasswordAuthenticationToken(email, senha);
    var auth = authManager.authenticate(authToken); // dispara UserDetailsService + encoder
    UserDetails principal = (UserDetails) auth.getPrincipal();
    return tokenService.generate(principal);
  }
}
