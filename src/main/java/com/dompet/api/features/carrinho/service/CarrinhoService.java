// CarrinhoService.java
// Implementa regras de negócio do carrinho: obtenção/criação, adição, atualização,
// remoção e operações por delta. Valida estoque e mapeia entidades para DTOs.
package com.dompet.api.features.carrinho.service;

import com.dompet.api.common.errors.*;
import com.dompet.api.features.carrinho.domain.*;
import com.dompet.api.features.carrinho.dto.*;
import com.dompet.api.features.carrinho.repo.CarrinhoRepository;
import com.dompet.api.features.carrinho.repo.ItemCarrinhoRepository;
import com.dompet.api.features.produtos.domain.Produtos;
import com.dompet.api.features.produtos.repo.ProdutosRepository;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;
import com.dompet.api.features.carrinho.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Regras de negócio do Carrinho.
 *
 * Responsabilidades:
 * - Criar/recuperar carrinho aberto por usuário
 * - Adicionar/atualizar/remover/limpar itens (API /cart)
 * - Alterar quantidade por delta (API /carrinho) com validações de estoque
 * - Mapear entidades para DTOs de resposta
 */
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

    /** Obtém carrinho aberto do usuário ou cria um novo. */
    @Transactional
    public Carrinho getOrCreateCart(String email) {
        return carrinhoRepo.findFirstByUsuarioEmailAndStatusOrderByUpdatedAtDesc(email, CartStatus.ABERTO)
                .orElseGet(() -> {
                    var user = usuariosRepo.findByEmail(email)
                            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
                    var c = new Carrinho();
                    c.setUsuario(user);
                    c.setStatus(CartStatus.ABERTO);
                    return carrinhoRepo.save(c);
                });
    }

    /** Retorna o carrinho aberto (criando se não existir) já mapeado em DTO. */
    @Transactional // permite criar carrinho (escrita) caso não exista
    public CartResponseDto getCart(String email) {
    var cart = carrinhoRepo.findFirstByUsuarioEmailAndStatusOrderByUpdatedAtDesc(email, CartStatus.ABERTO)
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

    /** Adiciona item ao carrinho, mesclando com existente e limitando ao estoque. */
    @Transactional
    public CartResponseDto addItem(String email, Long produtoId, Integer quantidade) {
        // valida quantidade de entrada
        if (quantidade == null || quantidade < 1) throw new IllegalArgumentException("Quantidade deve ser >= 1");
        var cart = getOrCreateCart(email);
        Produtos produto = produtosRepo.findById(produtoId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
        // evita adicionar produtos marcados como inativos
        if (Boolean.FALSE.equals(produto.getAtivo()))
            throw new IllegalArgumentException("Produto inativo");
        if (produto.getEstoque() == null || produto.getEstoque() < 1)
            throw new InsufficientStockException("Sem estoque", java.util.List.of(produto.getNome()));
        // merge item: se já existir, soma e limita ao estoque; caso contrário adiciona novo
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
        carrinhoRepo.save(cart); // JPA persiste alterações nos itens também (cascade/config de mapeamento)
        return getCart(email);
    }

    /** Atualiza a quantidade absoluta de um item; 0 remove. */
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

    /** Remove um item específico do carrinho. */
    @Transactional
    public void removeItem(String email, Long itemId) {
        var cart = getOrCreateCart(email);
        var item = cart.getItens().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Item não encontrado no seu carrinho"));
        cart.getItens().remove(item);
        itemRepo.deleteById(itemId);
        carrinhoRepo.save(cart);
    }

    /** Limpa todos os itens do carrinho aberto do usuário. */
    @Transactional
    public void clear(String email) {
        var cart = getOrCreateCart(email);
        cart.getItens().clear();
        carrinhoRepo.save(cart);
    }

    // ==== NOVO: alterarQuantidade por delta (carrinhoId, produtoId, delta) ====
    /**
     * Aplica um delta de quantidade para um produto dentro de um carrinho específico.
     * Regras:
     * - delta != 0
     * - Se item não existe e delta < 0: erro
     * - Resultado não pode ser negativo, nem exceder estoque
     * - Resultado 0 remove o item
     */
    @Transactional
    public com.dompet.api.features.carrinho.dto.CarrinhoDto alterarQuantidade(Long carrinhoId, Long produtoId, int delta) {
        if (delta == 0) throw new AlteracaoQuantidadeInvalidaException("delta não pode ser 0");

        var carrinho = carrinhoRepo.findWithItensById(carrinhoId)
                .orElseThrow(() -> new CarrinhoNaoEncontradoException("Carrinho não encontrado"));

        Produtos produto = produtosRepo.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        var itemOpt = itemRepo.findByCarrinhoIdAndProdutoId(carrinhoId, produtoId);

        if (itemOpt.isEmpty()) {
            if (delta < 0) throw new AlteracaoQuantidadeInvalidaException("Não é possível decrementar item inexistente");
            if (produto.getEstoque() == null || delta > produto.getEstoque())
                throw new EstoqueInsuficienteException("Quantidade solicitada excede o estoque disponível");
            var novo = new ItemCarrinho();
            novo.setCarrinho(carrinho);
            novo.setProduto(produto);
            novo.setQuantidade(delta);
            carrinho.getItens().add(novo);
        } else {
            var item = itemOpt.get();
            int atual = item.getQuantidade() == null ? 0 : item.getQuantidade();
            int result = atual + delta;
            if (result < 0) throw new AlteracaoQuantidadeInvalidaException("Quantidade resultante não pode ser negativa");
            if (produto.getEstoque() == null || result > produto.getEstoque())
                throw new EstoqueInsuficienteException("Quantidade resultante excede o estoque disponível");
            if (result == 0) {
                carrinho.getItens().remove(item);
                itemRepo.delete(item);
            } else {
                item.setQuantidade(result);
            }
        }

    carrinhoRepo.save(carrinho);

    // Mapeia carrinho para DTO de resposta
        var itensDto = carrinho.getItens().stream().map(i -> new com.dompet.api.features.carrinho.dto.ItemCarrinhoDto(
                i.getProduto().getId(),
                i.getProduto().getNome(),
                i.getProduto().getPreco(),
                i.getQuantidade(),
                i.getSubtotal()
        )).collect(java.util.stream.Collectors.toList());

        return new com.dompet.api.features.carrinho.dto.CarrinhoDto(
                carrinho.getId(),
                itensDto,
                carrinho.getTotal()
        );
    }
}
