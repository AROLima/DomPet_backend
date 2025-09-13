package com.dompet.api.features.pedidos.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import com.dompet.api.features.usuarios.domain.Usuarios;

@Entity
@Table(name = "pedidos")
public class Pedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuarios usuario;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private PedidoStatus status = PedidoStatus.NOVO;

  @Column(name = "total", precision = 19, scale = 2, nullable = false)
  private BigDecimal total = BigDecimal.ZERO;

  @Column(name = "observacoes", length = 1000)
  private String observacoes;

  @Column(name = "endereco_rua")
  private String enderecoRua;
  @Column(name = "endereco_numero")
  private String enderecoNumero;
  @Column(name = "endereco_bairro")
  private String enderecoBairro;
  @Column(name = "endereco_cep")
  private String enderecoCep;
  @Column(name = "endereco_cidade")
  private String enderecoCidade;

  private OffsetDateTime createdAt = OffsetDateTime.now();

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PedidoItem> itens = new ArrayList<>();

  public Long getId() { return id; }
  public Usuarios getUsuario() { return usuario; }
  public void setUsuario(Usuarios usuario) { this.usuario = usuario; }
  public PedidoStatus getStatus() { return status; }
  public void setStatus(PedidoStatus status) { this.status = status; }
  public BigDecimal getTotal() { return total; }
  public void setTotal(BigDecimal total) { this.total = total; }
  public String getObservacoes() { return observacoes; }
  public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
  public String getEnderecoRua() { return enderecoRua; }
  public void setEnderecoRua(String enderecoRua) { this.enderecoRua = enderecoRua; }
  public String getEnderecoNumero() { return enderecoNumero; }
  public void setEnderecoNumero(String enderecoNumero) { this.enderecoNumero = enderecoNumero; }
  public String getEnderecoBairro() { return enderecoBairro; }
  public void setEnderecoBairro(String enderecoBairro) { this.enderecoBairro = enderecoBairro; }
  public String getEnderecoCep() { return enderecoCep; }
  public void setEnderecoCep(String enderecoCep) { this.enderecoCep = enderecoCep; }
  public String getEnderecoCidade() { return enderecoCidade; }
  public void setEnderecoCidade(String enderecoCidade) { this.enderecoCidade = enderecoCidade; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public List<PedidoItem> getItens() { return itens; }

  public void addItem(PedidoItem item) {
    itens.add(item);
    item.setPedido(this);
  }
}
