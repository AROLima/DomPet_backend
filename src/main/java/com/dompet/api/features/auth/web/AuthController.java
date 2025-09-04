
// Arquivo: AuthController.java
// Objetivo: fornecer endpoints REST didáticos para autenticação.
// Este arquivo foi enriquecido com comentários linha-a-linha e cabeçalhos
// para servir como material de estudo; nenhum comportamento de runtime foi alterado.

package com.dompet.api.features.auth.web;

// Importações do Spring e utilitários do projeto
import org.springframework.http.*; // ResponseEntity, HttpStatus, HttpHeaders, etc.
import org.springframework.security.authentication.*; // AuthenticationManager, UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails; // Representa detalhes do usuário autenticado
import org.springframework.security.crypto.password.PasswordEncoder; // BCryptPasswordEncoder ou similar
import org.springframework.transaction.annotation.Transactional; // Controle de transação para operações persistentes
import org.springframework.web.bind.annotation.*; // @RestController, @RequestMapping, @PostMapping, @RequestBody
import org.springframework.http.CacheControl; // Controle de cache em respostas HTTP

// Importações de DTOs, serviços e entidades do projeto
import com.dompet.api.features.auth.dto.*; // AuthRegisterDto, AuthLoginDto, AuthResponseDto
import com.dompet.api.features.auth.token.TokenService; // Serviço responsável por gerar/validar JWTs
import com.dompet.api.features.usuarios.domain.Usuarios; // Entidade de usuário do domínio
import com.dompet.api.features.usuarios.repo.UsuariosRepository; // Repositório JPA para usuários
import com.dompet.api.features.usuarios.domain.Role; // Enum de perfis (roles)

// Importações para documentação Swagger/OpenAPI (metadados dos endpoints)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid; // Validação dos DTOs (Bean Validation)


/**
 * Controller de autenticação - Endpoints principais:
 * - POST /auth/register : registra usuário e retorna token
 * - POST /auth/login    : autentica e retorna token
 * - POST /auth/logout   : instrução para logout (cliente descarta token)
 * - POST /auth/logout-all : incrementa tokenVersion para invalidar tokens antigos
 * - POST /auth/refresh  : reemite token para o usuário autenticado
 *
 * Notas didáticas:
 * - O backend é stateless: os tokens JWT contém as claims necessárias.
 * - O TokenService encapsula geração/validação de tokens (algoritmo, secret, claims).
 * - Bump do tokenVersion é usado para implementar "logout em todos os dispositivos".
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticação e registro")
public class AuthController {

  // -------------------- Dependências --------------------
  // Repositório para operações CRUD em usuários
  private final UsuariosRepository usuariosRepo;
  // Encoder de senhas (BCrypt recomendado)
  private final PasswordEncoder encoder;
  // Componente do Spring que realiza a autenticação a partir de credenciais
  private final AuthenticationManager authManager;
  // Serviço responsável por criar/validar tokens JWT
  private final TokenService tokenService;


  /**
   * Construtor com injeção das dependências pelo Spring.
   * Recebe componentes necessários para lidar com persistência, autenticação
   * e geração de tokens.
   */
  public AuthController(UsuariosRepository usuariosRepo,
                        PasswordEncoder encoder,
                        AuthenticationManager authManager,
                        TokenService tokenService) {
    this.usuariosRepo = usuariosRepo;
    this.encoder = encoder;
    this.authManager = authManager;
    this.tokenService = tokenService;
  }


  // -------------------- Registro --------------------
  /**
   * Registra um novo usuário e realiza login automático.
   * Fluxo (passo-a-passo):
   * 1) Valida se já existe usuário com o email informado.
   * 2) Cria a entidade Usuarios preenchendo nome, email, senha(criptografada), role e flag ativo.
   * 3) Persiste a entidade no banco via UsuariosRepository.
   * 4) Autentica o usuário e gera um JWT retornado no body com código 201.
   *
   * Contrato:
   * - Input: AuthRegisterDto (nome, email, senha) — validado por Bean Validation (@Valid)
   * - Output: 201 Created + AuthResponseDto { token, expiresIn }
   * - Erros: 409 Conflict se email já existir
   */
  @PostMapping("/register")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
  @Operation(summary = "Registro de usuário")
  @ApiResponse(responseCode = "201", description = "Usuário registrado",
      content = @Content(schema = @Schema(implementation = com.dompet.api.features.auth.dto.AuthResponseDto.class),
          examples = @ExampleObject(value = "{\n  \"token\": \"<jwt>\",\n  \"expiresIn\": 3600000\n}")))
  @Transactional // Garante atomicidade da criação do usuário
  public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid AuthRegisterDto dto) {
    // Normaliza email (trim)
    final var email = dto.email().trim();
    // Se o email já existe, devolvemos 409 Conflict — evita duplicidade de contas
    if (usuariosRepo.existsByEmail(email)) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
    }

    // Monta a entidade Usuario a ser persistida
    var u = new Usuarios();
    u.setNome(dto.nome()); // atribui nome
  u.setEmail(email); // atribui email (único)
    // Nunca armazene senhas em texto claro — use PasswordEncoder para hash
    u.setSenha(encoder.encode(dto.senha()));
    u.setRole(Role.USER); // perfil padrão: USER
    u.setAtivo(true); // marca usuário como ativo

    // Persiste a entidade no banco
    usuariosRepo.save(u);

    // Login automático: autentica e gera token JWT
  var token = loginInternal(email, dto.senha());

    // Retorna 201 Created com token e header customizado X-API-Version
  return ResponseEntity.status(HttpStatus.CREATED)
    .cacheControl(CacheControl.noStore())
    .header("Pragma", "no-cache")
        .header("X-API-Version", "1")
        .body(new AuthResponseDto(token, tokenService.getExpirationMs()));
  }


  // -------------------- Login --------------------
  /**
   * Autentica credenciais e emite um JWT.
   * - Recebe email e senha via DTO.
   * - Faz delegação para loginInternal que usa AuthenticationManager.
   * - Retorna 200 OK com token em AuthResponseDto.
   */
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Login e emissão de JWT")
  @ApiResponse(responseCode = "200", description = "Login ok",
      content = @Content(schema = @Schema(implementation = com.dompet.api.features.auth.dto.AuthResponseDto.class),
          examples = @ExampleObject(value = "{\n  \"token\": \"<jwt>\",\n  \"expiresIn\": 3600000\n}")))
  public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthLoginDto dto) {
    // Delegamos a autenticação para o método auxiliar
    var token = loginInternal(dto.email().trim(), dto.senha());
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .header("Pragma", "no-cache")
        .header("X-API-Version", "1")
        .body(new AuthResponseDto(token, tokenService.getExpirationMs()));
  }


  // -------------------- Logout (cliente) --------------------
  /**
   * Logout simples: instruí o cliente a descartar o token JWT.
   * Observação:
   * - JWTs são stateless; não há sessão server-side a invalidar por padrão.
   * - Para invalidar tokens no servidor é preciso estratégia adicional (blacklist ou tokenVersion).
   */
  @PostMapping(value = "/logout")
  @Operation(summary = "Logout (cliente deve descartar o token)", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "204", description = "Logout efetuado")
  public ResponseEntity<Void> logout() {
    // Aqui apenas retornamos 204 No Content; o cliente deve deletar o token localmente
    return ResponseEntity.noContent()
        .cacheControl(CacheControl.noStore())
        .header("Pragma", "no-cache")
        .build();
  }


  // -------------------- Logout em todos os dispositivos --------------------
  /**
   * Implementa logout global incrementando o tokenVersion do usuário.
   * - A claim tokenVersion é emitida dentro do JWT e comparada na validação.
   * - Ao incrementar no banco, tokens antigos tornam-se inválidos.
   * - Necessita persistência (por isso método transacional).
   */
  @PostMapping(value = "/logout-all")
  @Transactional
  @Operation(summary = "Logout em todos os dispositivos (incrementa tokenVersion)", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "204", description = "Tokens anteriores invalidados")
  public ResponseEntity<Void> logoutAll(org.springframework.security.core.Authentication auth) {
    // auth contém o principal (username) quando o usuário está autenticado
    var email = auth.getName();
    var user = usuariosRepo.findByEmail(email).orElse(null);
    if (user == null) {
      // Se o usuário não for encontrado, não temos autorização para operar
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    // Incrementa tokenVersion para invalidar JWTs anteriores
    user.bumpTokenVersion();
    // Com @Transactional o JPA fará flush/commit ao final do método
  return ResponseEntity.noContent()
    .cacheControl(CacheControl.noStore())
    .header("Pragma", "no-cache")
    .build();
  }


  // -------------------- Refresh de token --------------------
  /**
   * Reemite um novo JWT para o usuário autenticado.
   * Fluxo:
   * - Verifica se auth está presente
   * - Recupera usuário no banco para garantir dados atuais (roles, ativo, tokenVersion)
   * - Cria UserDetails atualizados e gera novo token via TokenService
   */
  @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  @Operation(summary = "Refresh de JWT (reemitir token para o usuário atual)", security = { @SecurityRequirement(name = "bearerAuth") })
  @ApiResponse(responseCode = "200", description = "Token reemitido",
      content = @Content(schema = @Schema(implementation = com.dompet.api.features.auth.dto.AuthResponseDto.class),
          examples = @ExampleObject(value = "{\n  \"token\": \"<jwt>\",\n  \"expiresIn\": 3600000\n}")))
  public ResponseEntity<AuthResponseDto> refresh(org.springframework.security.core.Authentication auth) {
    // Se não autenticado, retorna 401
    if (auth == null || auth.getName() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Recupera usuário atual no banco (p. ex. para pegar roles atualizadas)
    var user = usuariosRepo.findByEmail(auth.getName()).orElse(null);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Cria um UserDetails baseado nos dados persistidos (necessário para TokenService)
    var principal = org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getSenha())
        .authorities(user.getRole().getAuthorities())
        .build();

    // Gera novo token e retorna ao cliente
    var token = tokenService.generate(principal);
    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .header("Pragma", "no-cache")
      .header("X-API-Version", "1")
      .body(new AuthResponseDto(token, tokenService.getExpirationMs()));
  }


  // -------------------- Helpers internos --------------------
  /**
   * Autentica credenciais e retorna o JWT gerado.
   * - Cria um UsernamePasswordAuthenticationToken com email e senha.
   * - Chama AuthenticationManager.authenticate(), que delega ao UserDetailsService
   *   e ao PasswordEncoder para verificar a credencial.
   * - Recupera o principal (UserDetails) e passa para TokenService.generate().
   */
  private String loginInternal(String email, String senha) {
    // Monta o token de autenticação com as credenciais recebidas
    var authToken = new UsernamePasswordAuthenticationToken(email, senha);
    // Executa a autenticação: lança AuthenticationException em caso de falha
    var auth = authManager.authenticate(authToken);
    // Recupera UserDetails (username, password, authorities)
    UserDetails principal = (UserDetails) auth.getPrincipal();
    // Gera e retorna o JWT a partir do principal autenticado
    return tokenService.generate(principal);
  }


}
