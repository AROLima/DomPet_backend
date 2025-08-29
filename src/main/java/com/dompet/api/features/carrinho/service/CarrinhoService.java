package com.dompet.api.features.carrinho.service;

import com.dompet.api.common.errors.*;
import com.dompet.api.features.carrinho.domain.*;
import com.dompet.api.features.carrinho.dto.*;
import com.dompet.api.features.carrinho.repo.CarrinhoRepository;
import com.dompet.api.features.carrinho.repo.ItemCarrinhoRepository;
import com.dompet.api.features.produtos.domain.Produtos;
import com.dompet.api.features.produtos.repo.ProdutosRepository;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepo;
    private final ItemCarrinhoRepository itemRepo;
    private final ProdutosRepository produtosRepo;
    private final UsuariosRepository usuariosRepo;

    public CarrinhoService(CarrinhoRepository carrinhoRepo, ItemCarrinhoRepository itemRepo,
                           ProdutosRepository produtosRepo, UsuariosRepository usuariosRepo) {
        this.carrinhoRepo = carrinhoRepo;
        this.itemRepo = itemRepo;
        this.produtosRepo = produtosRepo;
        this.usuariosRepo = usuariosRepo;
    }

    @Transactional
    public Carrinho getOrCreateCart(String email) {
        return carrinhoRepo.findByUsuarioEmailAndStatus(email, CartStatus.ABERTO)
                .orElseGet(() -> {
                    var user = usuariosRepo.findByEmail(email)
                            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
                    var c = new Carrinho();
                    c.setUsuario(user);
                    c.setStatus(CartStatus.ABERTO);
                    return carrinhoRepo.save(c);
                });
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCart(String email) {
        var cart = carrinhoRepo.findByUsuarioEmailAndStatus(email, CartStatus.ABERTO)
                .orElseGet(() -> getOrCreateCart(email));
        var items = cart.getItens().stream().map(i -> new CartResponseDto.ItemDto(
                i.getId(),
                i.getProduto().getId(),
                i.getProduto().getNome(),
                i.getProduto().getPreco(),
                i.getQuantidade(),
                i.getSubtotal()
        )).collect(Collectors.toList());
        return new CartResponseDto(cart.getId(), items, cart.getTotal());
    }

    @Transactional
    public CartResponseDto addItem(String email, Long produtoId, Integer quantidade) {
        if (quantidade == null || quantidade < 1) throw new IllegalArgumentException("Quantidade deve ser >= 1");
        var cart = getOrCreateCart(email);
        Produtos produto = produtosRepo.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
        if (Boolean.FALSE.equals(produto.getAtivo()))
            throw new IllegalArgumentException("Produto inativo");
        if (produto.getEstoque() == null || produto.getEstoque() < 1)
            throw new InsufficientStockException("Sem estoque", java.util.List.of(produto.getNome()));

        // merge item
        var existing = cart.getItens().stream().filter(i -> i.getProduto().getId().equals(produtoId)).findFirst();
        if (existing.isPresent()) {
            int novaQtd = existing.get().getQuantidade() + quantidade;
            int max = produto.getEstoque();
            if (novaQtd > max) novaQtd = max; // limita ao estoque
            existing.get().setQuantidade(novaQtd);
        } else {
            var item = new ItemCarrinho();
            item.setCarrinho(cart);
            item.setProduto(produto);
            item.setQuantidade(Math.min(quantidade, produto.getEstoque()));
            cart.getItens().add(item);
        }
        carrinhoRepo.save(cart);
        return getCart(email);
    }

    @Transactional
    public CartResponseDto updateItem(String email, Long itemId, Integer quantidade) {
        var cart = getOrCreateCart(email);
        var item = cart.getItens().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Item não encontrado no seu carrinho"));
        if (!cart.getUsuario().getEmail().equals(email))
            throw new ForbiddenException("Carrinho de outro usuário");
        if (quantidade == null || quantidade < 0) throw new IllegalArgumentException("Quantidade inválida");
        if (quantidade == 0) {
            cart.getItens().remove(item);
            itemRepo.deleteById(itemId);
        } else {
            var produto = item.getProduto();
            if (Boolean.FALSE.equals(produto.getAtivo())) throw new IllegalArgumentException("Produto inativo");
            if (quantidade > produto.getEstoque()) throw new InsufficientStockException("Estoque insuficiente",
                    java.util.List.of(produto.getNome()));
            item.setQuantidade(quantidade);
        }
        carrinhoRepo.save(cart);
        return getCart(email);
    }

    @Transactional
    public void removeItem(String email, Long itemId) {
        var cart = getOrCreateCart(email);
        var item = cart.getItens().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Item não encontrado no seu carrinho"));
        cart.getItens().remove(item);
        itemRepo.deleteById(itemId);
        carrinhoRepo.save(cart);
    }

    @Transactional
    public void clear(String email) {
        var cart = getOrCreateCart(email);
        cart.getItens().clear();
        carrinhoRepo.save(cart);
    }
}
