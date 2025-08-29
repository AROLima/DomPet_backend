# DomPet API – Architecture Overview

This document summarizes the code structure and request flow so you can navigate the project quickly.

## Stack
- Spring Boot 3, Java 21
- Spring Web, Spring Data JPA, Spring Security (JWT)
- Bean Validation (Jakarta), Lombok, springdoc-openapi

## Package layout (feature-first)
- `com.dompet.api.common`: cross-cutting (config, errors)
- `com.dompet.api.features.<feature>`: isolated feature modules
  - `auth` → login/register, JWT
  - `produtos` → catalog CRUD
  - `carrinho` → cart lifecycle and delta quantity
  - `pedidos` → checkout and order management
- `com.dompet.api.shared`: shared value objects (e.g., Endereco)

## Security
- Stateless JWT with HS256
- JwtAuthFilter extracts token, validates signature/exp, checks tokenVersion claim against DB, and sets authentication
- Public routes: `/auth/login`, `/auth/register`, swagger, h2 console, and `GET /produtos/**`
- Auth required: `/auth/logout`, `/auth/logout-all`, `/cart/**`, `/carrinho/**`, `/pedidos/**`
- ADMIN required: product mutations and `PATCH /pedidos/**`

## Error handling
- `ApiErrors` maps exceptions to RFC7807 ProblemDetail responses
- Validation errors aggregate field violations
- Cart-specific errors mapped to 404/400/409 appropriately

## Produtos
- Controller is thin and delegates to `ProdutosService`
- Non-paginated list on `GET /produtos` (compatibility)
- Paginated search on `GET /produtos/search` with `ativo=true`
- DTOs: Create/Update/Read to avoid leaking entities

## Carrinho
- Two APIs:
  - `/cart` bound to the current authenticated user (CRUD of items)
  - `/carrinho` for delta operations on specific cartId + produtoId
- `CarrinhoService` enforces stock rules, transactional updates, and DTO mapping

## Pedidos
- `PedidoService.checkout` validates stock, snapshots prices, creates order, decrements stock, closes cart
- Status transitions enforced with `isTransitionAllowed`; cancel before shipping restores stock
- Admin-only status updates

## DTO & Entities
- Entities are JPA-managed and not exposed directly
- DTOs define the public contract and validation

## Development utilities
- Swagger UI: `/swagger-ui.html`
- Insomnia collection in `docs/Insomnia_DomPet_API.json`

## Sequence flows (text)

Login (POST /auth/login)
1) Client → AuthController.login: email/senha
2) AuthController → AuthenticationManager: autentica UserDetails (DbUserDetailsService + PasswordEncoder)
3) AuthController → TokenService.generate: cria JWT com sub=email, roles, ver=tokenVersion
4) AuthController → Client: 200 { token }

Authenticated request (any protected route)
1) Client → API: Authorization: Bearer <token>
2) JwtAuthFilter.resolveToken: extrai token
3) JwtAuthFilter.parseClaims: valida assinatura/exp; lê sub/ver
4) JwtAuthFilter → UsuariosRepository: carrega usuário por email
5) JwtAuthFilter: compara ver (DB) vs claim (token); se OK, seta Authentication no contexto
6) Encaminha para controller de destino

Add to cart (POST /cart/items)
1) Client → CartController.addItem (com Bearer)
2) JwtAuthFilter autentica e injeta Authentication
3) CartController → CarrinhoService.addItem(email, produtoId, quantidade)
4) Service → getOrCreateCart(email) via CarrinhoRepository/UsuariosRepository
5) Service → ProdutosRepository.findById: valida ativo/estoque
6) Service: mescla item (ou cria), limita à disponibilidade; salva Carrinho
7) Service → getCart(email): monta DTO (itens, total)
8) CartController → Client: 200 CartResponseDto

Delta quantity (PATCH /carrinho/{carrinhoId}/itens/{produtoId}?delta=)
1) Client → CarrinhoController.alterarQuantidade (com Bearer)
2) JwtAuthFilter autentica
3) Controller → CarrinhoService.alterarQuantidade(carrinhoId, produtoId, delta)
4) Service: busca carrinho com itens (EntityGraph), valida produto e regras:
  - delta != 0; não decrementar item inexistente; 0 remove; limite por estoque
5) Service: aplica alterações e salva; mapeia para CarrinhoDto
6) Controller → Client: 200 CarrinhoDto

Checkout (POST /pedidos/checkout)
1) Client → PedidosController.checkout (com Bearer)
2) JwtAuthFilter autentica
3) Controller → PedidoService.checkout(email, dto)
4) Service: carrega carrinho ABERTO; erro se vazio
5) Service: recarrega produtos; valida ativo/estoque para cada item
6) Service: cria Pedido com itens (snapshot de preço), calcula total; debita estoque
7) Service: fecha carrinho (status=FECHADO) e limpa itens; salva pedido
8) Service: mapeia para PedidoResponseDto
9) Controller → Client: 201 PedidoResponseDto

