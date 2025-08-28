```mermaid
erDiagram
  USUARIOS ||--o{ PEDIDOS : "faz"
  PEDIDOS  ||--|{ ITEM_PEDIDO : "contém"
  PRODUTOS ||--o{ ITEM_PEDIDO : "referenciado por"

  USUARIOS {
    int id PK
    string nome
    string email
    string senha
    string role
    boolean ativo
  }

  PRODUTOS {
    int id PK
    string nome
    string descricao
    float  preco          %% BigDecimal/DECIMAL(10,2) no banco
    int    estoque
    string imagem_url
    string categoria      %% enum no código
    boolean ativo
  }

  PEDIDOS {
    int id PK
    int usuario_id FK
    string status         %% enum no código
    string endereco_logradouro
    string endereco_numero
    string endereco_complemento
    string endereco_bairro
    string endereco_cidade
    string endereco_estado
    string endereco_cep
    boolean ativo
  }

  ITEM_PEDIDO {
    int id PK
    int pedido_id FK
    int produto_id FK
    int quantidade
    float preco_unitario  %% BigDecimal/DECIMAL(10,2) no banco
  }
```

### Classes (JPA/domínio)
```mermaid
classDiagram
  class Usuarios {
    +Long id
    +String nome
    +String email
    +String senha
    +String role
    +Boolean ativo
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
  }

  class Pedidos {
    +Long id
    +Usuarios usuario
    +StatusPedido status
    +Endereco enderecoEntrega
    +List~ItemPedido~ itens
    +Boolean ativo
    +void excluir()
    +void alterarStatus(StatusPedido)
  }

  class ItemPedido {
    +Long id
    +Pedidos pedido
    +Produtos produto
    +Integer quantidade
    +BigDecimal precoUnitario
    +BigDecimal subtotal()
  }

  class Endereco {
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
  Pedidos "1" --> "1..*" ItemPedido : contém
  Produtos "1" --> "0..*" ItemPedido : referenciado-por
  ItemPedido "many" --> "1" Produtos : produto
  ItemPedido "many" --> "1" Pedidos : pedido
  Produtos --> Categorias : enum
  Pedidos --> StatusPedido : enum
```
