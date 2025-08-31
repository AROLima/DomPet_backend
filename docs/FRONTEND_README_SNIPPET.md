# DomPet Frontend — Notas de Integração com a API

Estas notas destinam-se ao repositório do frontend (Flutter). Copie/cole no `README.md` do front ou incorpore conforme preferir.

## Backends e Autorização
- Base URL dev: `http://localhost:8080`
- Autorização: Bearer JWT. Obtenha via `POST /auth/login`.
- CORS: liberado em dev. Em prod, alinhar origem no backend.

## Rotas principais consumidas
- Produtos
  - `GET /produtos` (filtros opcionais `?nome=`, `?categoria=`)
  - `GET /produtos/search?page=&size=&sort=&nome=&categoria=` (paginado)
  - `GET /produtos/{id}` (suporta ETag/If-None-Match -> 304 Not Modified)
  - `GET /produtos/suggestions?q=&limit=` (autocomplete leve)
  - Admin: `POST /produtos`, `PUT /produtos/{id}`, `DELETE /produtos/{id}` (ROLE_ADMIN)
- Carrinho
  - `GET /cart`, `POST /cart/items`, `PATCH /cart/items/{itemId}`, `DELETE /cart/items/{itemId}`, `DELETE /cart`
  - Delta: `PATCH /carrinho/{carrinhoId}/itens/{produtoId}?delta=`, `.../incrementar?by=`, `.../decrementar?by=`
- Pedidos
  - `POST /pedidos/checkout`, `GET /pedidos`, `GET /pedidos/{id}`
  - Admin: `PATCH /pedidos/{id}/status`
- Usuários
  - `GET /usuarios/me` (dados do usuário autenticado)

## Swagger / OpenAPI
- UI: `http://localhost:8080/swagger-ui.html`
- JSON: `http://localhost:8080/v3/api-docs`

## Dicas de cliente HTTP
- Envie `If-None-Match` em `GET /produtos/{id}` com o valor do cabeçalho `ETag` anterior para economizar tráfego.
- Trate erros no formato `application/problem+json` com os campos: `status`, `title`, `detail`, e opcional `errors[]` (validação).

## Perfis e Guardas (Frontend)
- Rotas de Admin exigem `ROLE_ADMIN` (ou `ADMIN`). Decodifique o JWT para extrair `roles`.
- Quando o JWT não carregar `roles`, consulte `/usuarios/me` e derive permissões do campo `role`.
