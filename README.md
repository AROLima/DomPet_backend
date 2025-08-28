```mermaid
erDiagram
  USUARIOS ||--o{ PEDIDOS : "faz"
  PEDIDOS  ||--|{ ITEM_PEDIDO : "contém"
  PRODUTOS ||--o{ ITEM_PEDIDO : "referenciado por"

  USUARIOS {
    LONG id PK
    STRING nome
    STRING email
    STRING senha
    STRING role
    BOOLEAN ativo
  }

  PRODUTOS {
    LONG id PK
    STRING nome
    TEXT  descricao
    DECIMAL(10,2) preco
    INT   estoque
    STRING imagem_url
    ENUM  categoria   "RACAO | HIGIENE | MEDICAMENTOS | ACESSORIOS | BRINQUEDOS"
    BOOLEAN ativo
  }

  PEDIDOS {
    LONG id PK
    LONG usuario_id FK
    ENUM status      "AGUARDANDO_PAGAMENTO | PAGO | ENVIADO | ENTREGUE | CANCELADO"
    -- Endereco embutido (campos abaixo)
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
    DECIMAL(10,2) preco_unitario
    -- subtotal é calculado
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
