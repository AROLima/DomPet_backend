package com.dompet.api.features.pedidos.domain;

import java.math.BigDecimal;
import jakarta.persistence.*;
import com.dompet.api.features.produtos.domain.Produtos;

@Entity
@Table(name = "item_pedido")
public class PedidoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "pedido_id", nullable = false)
  private Pedido pedido;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "produto_id", nullable = false)
  private Produtos produto;

  @Column(name = "nome_produto", nullable = false)
  private String nomeProduto;

  @Column(name = "preco_unitario", precision = 19, scale = 2, nullable = false)
  private BigDecimal precoUnitario;

  @Column(name = "quantidade", nullable = false)
  private Integer quantidade;

  @Column(name = "subtotal", precision = 19, scale = 2, nullable = false)
  private BigDecimal subtotal;

  public Long getId() { return id; }
  public Pedido getPedido() { return pedido; }
  public void setPedido(Pedido pedido) { this.pedido = pedido; }
  public Produtos getProduto() { return produto; }
  public void setProduto(Produtos produto) { this.produto = produto; }
  public String getNomeProduto() { return nomeProduto; }
  public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
  public BigDecimal getPrecoUnitario() { return precoUnitario; }
  public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
  public Integer getQuantidade() { return quantidade; }
  public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
  public BigDecimal getSubtotal() { return subtotal; }
  public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
