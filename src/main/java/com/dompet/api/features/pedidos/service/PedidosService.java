package com.dompet.api.features.pedidos.service;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dompet.api.features.carrinho.service.CarrinhoService;
import com.dompet.api.features.pedidos.domain.Pedido;
import com.dompet.api.features.pedidos.domain.PedidoItem;
import com.dompet.api.features.pedidos.domain.PedidoStatus;
import com.dompet.api.features.pedidos.dto.CheckoutRequestDto;
import com.dompet.api.features.pedidos.dto.PedidoDto;
import com.dompet.api.features.pedidos.mapper.PedidoMapper;
import com.dompet.api.features.pedidos.repo.PedidosRepository;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;
import com.dompet.api.features.carrinho.repo.CarrinhoRepository;
import com.dompet.api.features.carrinho.domain.Carrinho;
import com.dompet.api.features.carrinho.errors.CarrinhoVazioException;
import com.dompet.api.features.produtos.domain.Produtos;
import com.dompet.api.common.errors.InsufficientStockException;

@Service
public class PedidosService {
  private final PedidosRepository pedidosRepo;
  private final UsuariosRepository usuariosRepo;
  private final CarrinhoService carrinhoService;
  private final CarrinhoRepository carrinhoRepo;

  public PedidosService(PedidosRepository pedidosRepo, UsuariosRepository usuariosRepo,
      CarrinhoService carrinhoService, CarrinhoRepository carrinhoRepo) {
    this.pedidosRepo = pedidosRepo;
    this.usuariosRepo = usuariosRepo;
    this.carrinhoService = carrinhoService;
    this.carrinhoRepo = carrinhoRepo;
  }

  @Transactional
  public PedidoDto checkout(String email, CheckoutRequestDto dto) {
  var user = usuariosRepo.findByEmailIgnoreCase(email).orElseThrow();
    // Obtém carrinho aberto (cria se não existir) para garantir estado consistente
    Carrinho cart = carrinhoService.getOrCreateCart(email);
    if (cart.getItens().isEmpty()) throw new CarrinhoVazioException();
    var pedido = new Pedido();
    pedido.setUsuario(user);
    pedido.setStatus(PedidoStatus.NOVO);
    pedido.setObservacoes(dto.observacoes());
    pedido.setEnderecoRua(dto.rua());
    pedido.setEnderecoNumero(dto.numero());
    pedido.setEnderecoBairro(dto.bairro());
    pedido.setEnderecoCep(dto.cep());
    pedido.setEnderecoCidade(dto.cidade());

    BigDecimal total = BigDecimal.ZERO;
    // Primeiro valida estoque de todos os itens para evitar decrementar parcialmente
    var semEstoque = new java.util.ArrayList<String>();
    cart.getItens().forEach(ci -> {
      Produtos p = ci.getProduto();
      Integer disponivel = p.getEstoque();
      if (disponivel == null || disponivel < ci.getQuantidade()) {
        semEstoque.add(p.getNome());
      }
    });
    if (!semEstoque.isEmpty()) {
      throw new InsufficientStockException("Estoque insuficiente", semEstoque);
    }
    // Mapeia itens e decrementa estoque
    cart.getItens().forEach(ci -> {
      Produtos p = ci.getProduto();
      p.setEstoque(p.getEstoque() - ci.getQuantidade());
      var it = new PedidoItem();
      it.setProduto(p);
      it.setNomeProduto(p.getNome());
      it.setPrecoUnitario(p.getPreco());
      it.setQuantidade(ci.getQuantidade());
      it.setSubtotal(p.getPreco().multiply(BigDecimal.valueOf(ci.getQuantidade())));
      pedido.addItem(it);
    });
    for (var i : pedido.getItens()) {
      total = total.add(i.getSubtotal());
    }
    pedido.setTotal(total);
    pedidosRepo.save(pedido);
    // Limpa carrinho: remove itens e persiste
    cart.getItens().clear();
    carrinhoRepo.save(cart);
    return PedidoMapper.toDto(pedido);
  }

  @Transactional(readOnly = true)
  public Page<PedidoDto> listForUser(String email, Pageable pageable) {
    return pedidosRepo.findByUsuarioEmailOrderByCreatedAtDesc(email, pageable).map(PedidoMapper::toDto);
  }

  @Transactional(readOnly = true)
  public PedidoDto getOne(String email, Long id) {
    var p = pedidosRepo.findById(id).orElseThrow();
    if (!p.getUsuario().getEmail().equals(email)) throw new IllegalArgumentException("Pedido nao pertence ao usuario");
    return PedidoMapper.toDto(p);
  }

  @Transactional
  public PedidoDto updateStatus(Long id, PedidoStatus novoStatus) {
    var p = pedidosRepo.findById(id).orElseThrow();
    p.setStatus(novoStatus);
    pedidosRepo.save(p);
    return PedidoMapper.toDto(p);
  }
}
