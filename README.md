# Dompet API — E-commerce (Spring Boot)

> Backend para e-commerce com **estrutura por feature**, **JWT**, **H2**, **Swagger** e exemplos de **seed**

## ⚙️ Stack
- **Java 21**, **Spring Boot 3.5**
- Spring Web, Spring Data JPA, H2
- Spring Security + **JWT (jjwt)**
- Bean Validation (Jakarta)
- Lombok
- Swagger / OpenAPI

---

## 🔗 Links rápidos

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

---

## 📁 Estrutura (feature‑first)

```
com.dompet.api
├─ common/
│  ├─ config/                 # Security, Swagger, CORS, Jackson, etc.
│  └─ errors/                 # Exceptions + handler global
├─ shared/
│  └─ endereco/Endereco.java  # @Embeddable reutilizado
└─ features/
   ├─ auth/                   # DTOs, controller, filtro JWT, TokenService
   ├─ produtos/               # domain, dto, repo, web
   ├─ carrinho/               # domain (Carrinho, ItemCarrinho), dto, repo, service, web
   ├─ pedidos/                # domain (Pedido, ItemPedido), dto, repo, service, web
   └─ usuarios/               # domain, repo, web
```

> Mantemos cada **feature** com: `domain/`, `dto/`, `repo/`, `service/ (opcional)`, `web/`.

---

## 🔐 Autenticação (JWT)

### Dependências (pom.xml)
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
```

### Propriedades
```properties
# JWT
app.jwt.secret=COLA_AQUI_UMA_CHAVE_BASE64_DE_32_BYTES
app.jwt.expiration-ms=3600000  # 1h

# H2 (dev)
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

**Gerar chave** (32 bytes Base64 / HS256):
- **Windows (PowerShell):**
  ```powershell
  [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
  ```
- **Linux/macOS (OpenSSL):**
  ```bash
  openssl rand -base64 32
  ```

> No código, a senha do usuário é **hasheada** com BCrypt e **não** é reversível.

---

## 🗃️ Modelo de domínio

### ER diagram
```mermaid
erDiagram
  USUARIOS ||--o{ PEDIDOS : "faz"
  PEDIDOS  ||--|{ ITEM_PEDIDO : "contém"
  PRODUTOS ||--o{ ITEM_PEDIDO : "é referenciado por"

  USUARIOS {
    LONG id PK
    STRING nome
    STRING email
    STRING senha
    STRING role
    BOOLEAN ativo
    INT token_version
  }

  PRODUTOS {
    LONG id PK
    STRING nome
    TEXT  descricao
    DECIMAL preco
    INT   estoque
    STRING imagem_url
    ENUM  categoria   "RACAO | HIGIENE | MEDICAMENTOS | ACESSORIOS | BRINQUEDOS"
    BOOLEAN ativo
  }

  PEDIDOS {
    LONG id PK
    LONG usuario_id FK
    ENUM status      "AGUARDANDO_PAGAMENTO | PAGO | ENVIADO | ENTREGUE | CANCELADO"
    STRING endereco_logradouro
    STRING endereco_numero
    STRING endereco_complemento
    STRING endereco_bairro
    STRING endereco_cidade
    STRING endereco_estado
    STRING endereco_cep
    BOOLEAN ativo
  }

  ITEM_PEDIDO {
    LONG id PK
    LONG pedido_id FK
    LONG produto_id FK
    INT  quantidade
    DECIMAL preco_unitario
  }
```

### Class diagram (JPA + enums + embutidos)
```mermaid
classDiagram
  class Usuarios {
    +Long id
    +String nome
    +String email
    +String senha
    +String role
    +Boolean ativo
    +Integer tokenVersion
    +void bumpTokenVersion()
  }

  class Produtos {
    +Long id
    +String nome
    +String descricao
    +BigDecimal preco
    +Integer estoque
    +String imagemUrl
    +Categorias categoria
    +Boolean ativo
    +void atualizarInformacoes(ProdutosDto)
    +void excluir()
    +void restaurar()
  }

  class Pedidos {
    +Long id
    +Usuarios usuario
    +StatusPedido status
    +Endereco enderecoEntrega
    +List~ItemPedido~ itens
    +Boolean ativo
    +void excluir()
  }

  class ItemPedido {
    +Long id
    +Pedidos pedido
    +Produtos produto
    +Integer quantidade
    +BigDecimal precoUnitario
  }

  class Endereco {
    <<@Embeddable>>
    +String logradouro
    +String numero
    +String complemento
    +String bairro
    +String cidade
    +String estado
    +String cep
  }

  class Categorias {
    <<enumeration>>
    RACAO
    HIGIENE
    MEDICAMENTOS
    ACESSORIOS
    BRINQUEDOS
  }

  class StatusPedido {
    <<enumeration>>
    AGUARDANDO_PAGAMENTO
    PAGO
    ENVIADO
    ENTREGUE
    CANCELADO
  }

  Usuarios "1" --> "0..*" Pedidos : faz
  Pedidos "1" --> "1" Endereco : embute
  Pedidos "1" --> "1..*" ItemPedido : contem
  Produtos "1" --> "0..*" ItemPedido : referenciadoPor
  ItemPedido "many" --> "1" Produtos : produto
  ItemPedido "many" --> "1" Pedidos : pedido
  Produtos --> Categorias : enum
  Pedidos --> StatusPedido : enum
```

---

## 🚀 Como rodar

### Pré‑requisitos
- Java 21, Maven
- (Dev) H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:dompet`)

### Iniciar a API (Windows/PowerShell)
- Via Maven Wrapper (sem Maven instalado):
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
- Ou gerando o jar:
  ```powershell
  .\mvnw.cmd -q -DskipTests package
  java -jar .\target\api-*.jar
  ```

## 📖 Swagger / OpenAPI
- **UI**: `http://localhost:8080/swagger-ui.html`
- **Docs**: `http://localhost:8080/v3/api-docs`


---

## 🌱 Seed (data.sql)

### Usuários (BCrypt entre parênteses a senha para teste)
```sql
-- ADMIN (senha: Admin@123)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Admin', 'admin@dompet.dev', '$2b$10$E.iUyby25SywbG1jyoHM7uoIDs6mhFnIPDkyTg.4g9Ove6gCp3/py', 'ADMIN', TRUE);

-- RODRIGO (senha: 123456)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Rodrigo', 'rodrigo@dompet.dev', '$2b$10$7m9nB.s8kppeEhvJ9z/wFuK1eMzH5NIo4TqI1g9XJ/CKLmeILJEvq', 'USER', TRUE);

-- JÚLIA (senha: 123456)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Júlia', 'julia@dompet.dev', '$2b$10$cZ3D8Ou65Ps/ryh0tV28fu/0g57FruZhZwp4AnIizLNusjfqCQJEm', 'USER', TRUE);

-- CARLOS (senha: cliente123)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Carlos', 'carlos@dompet.dev', '$2b$10$IOalqC2na454mb7tay7EJei9McMbEkGYfoeyDm.bpuKZPKEEZH5fe', 'USER', TRUE);

-- MARIA (senha: cliente123)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Maria', 'maria@dompet.dev', '$2b$10$Z.BIOEnOYI3bfXtSeTcOtOQIZItTSv24GaP2HR69evFdSVwmUIwDi', 'USER', TRUE);
```

> Você pode adicionar um bloco de produtos conforme sua `entity Produtos` (preço como **BigDecimal**).

---

## 🔌 Endpoints (resumo)

### Auth
- `POST /auth/register` → cria usuário e retorna **JWT**
  ```json
  { "nome":"Rodrigo", "email":"rodrigo@dompet.dev", "senha":"123456" }
  ```
- `POST /auth/login` → retorna **JWT**
  ```json
  { "email":"rodrigo@dompet.dev", "senha":"123456" }
  ```
- `POST /auth/logout` → 204 No Content (stateless). Cliente deve descartar o token.
- `POST /auth/logout-all` → 204; incrementa tokenVersion. Tokens antigos passam a 401.

### Produtos
- `GET /produtos` — público; filtros opcionais:
  - `?categoria=RACAO`
  - `?nome=golden`
- `GET /produtos/{id}` — público
- `POST /produtos` — **ADMIN** (envie `Authorization: Bearer <token>`)
- `PUT /produtos/{id}` — **ADMIN**
- `DELETE /produtos/{id}` — **ADMIN** (soft delete `ativo=false`)
- `PATCH /produtos/{id}/ativar` — **ADMIN** (undo do soft delete)

**Exemplo de criação:**
```json
{
  "nome": "Ração Golden",
  "descricao": "Ração premium para cães adultos",
  "preco": 129.90,
  "estoque": 15,
  "imagemUrl": "https://meuecommerce.com/imagens/racao.png",
  "ativo": true,
  "categoria": "RACAO"
}
```

### Carrinho
- Todas exigem Authorization: Bearer <token>
- `GET /cart` — retorna o carrinho atual (ABERTO) do usuário
- `POST /cart/items` — adiciona/mescla item
  ```json
  { "produtoId": 1, "quantidade": 2 }
  ```
- `PATCH /cart/items/{itemId}` — atualiza quantidade (0 remove)
  ```json
  { "quantidade": 3 }
  ```
- `DELETE /cart/items/{itemId}` — remove item
- `DELETE /cart` — limpa carrinho

### Pedidos
- `POST /pedidos/checkout` — cria pedido a partir do carrinho e zera o carrinho
  ```json
  {
    "enderecoEntrega": {
      "logradouro": "Rua A",
      "numero": "100",
      "complemento": "ap 12",
      "bairro": "Centro",
      "cidade": "SP",
      "estado": "SP",
      "cep": "01000-000"
    },
    "observacoes": "Entregar à tarde",
    "metodoPagamento": "CARTAO"
  }
  ```
- `GET /pedidos` — lista meus pedidos (paginado)
- `GET /pedidos/{id}` — detalhe (dono ou ADMIN)
- `PATCH /pedidos/{id}/status` — ADMIN (ex.: `PAGO`, `ENVIADO`, `ENTREGUE`, `CANCELADO`)

### Insomnia/Postman
1. **POST** `/auth/register` ou `/auth/login` → copie `token` do JSON
2. **GET** `/produtos` (público)
3. **POST** `/cart/items` (com Bearer)
4. **GET** `/cart`
5. **POST** `/pedidos/checkout`
6. **GET** `/pedidos`

Coleção pronta (import): `docs/Insomnia_DomPet_API.json`.

---

## 🔐 Fluxos (Mermaid)

### Login e uso de token
```mermaid
sequenceDiagram
  autonumber
  participant C as Cliente (App/Web)
  participant API as Dompet API
  C->>API: POST /auth/login { email, senha }
  API-->>C: 200 { token }
  Note right of C: Armazena token e envia no header Authorization: Bearer <token>
  C->>API: GET /produtos (Authorization: Bearer ...)
  API-->>C: 200 lista
```

### Logout-all com tokenVersion
```mermaid
sequenceDiagram
  autonumber
  participant C as Cliente (token A)
  participant API as Dompet API
  participant DB as DB (usuarios.token_version)
  C->>API: POST /auth/logout-all (Authorization: Bearer token A)
  API->>DB: usuarios.bumpTokenVersion()
  API-->>C: 204 No Content
  Note over DB: token_version incrementado (ex.: 0 → 1)
  C->>API: GET /pedidos (Authorization: Bearer token A)
  API-->>C: 401 Unauthorized (ver do token != token_version)
  C->>API: POST /auth/login (novamente)
  API-->>C: 200 { token B (ver=1) }
  C->>API: GET /pedidos (Authorization: Bearer token B)
  API-->>C: 200 OK
```

---

## ✅ Boas práticas já aplicadas
- `BigDecimal` para preços (`@Column(precision=10, scale=2)`)
- Soft delete (`ativo=true/false`) + endpoints de **ativar/excluir**
- DTOs com validação (@NotBlank/@NotNull/@PositiveOrZero)
- Enum `Categorias` persistido como `STRING`
- Filtro JWT `OncePerRequestFilter` + Security stateless
- Erros de validação e domínio centralizados em handler global

---

## 🗺️ Notas
- `schema.sql` garante coluna `usuarios.token_version` com default em bancos que precisarem.

---

## 📄 Licença
Livre para estudo e evolução. ✨
