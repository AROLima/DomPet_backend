// PedidoService.java
// Serviço que implementa o fluxo de checkout: valida estoque, cria pedido com snapshot
// de preços, debita estoque, fecha carrinho e provê métodos para consulta e alteração de status.
package com.dompet.api.features.pedidos.service;

import com.dompet.api.common.errors.*;
import com.dompet.api.features.carrinho.domain.*;
import com.dompet.api.features.carrinho.repo.CarrinhoRepository;
import com.dompet.api.features.pedidos.domain.*;
import com.dompet.api.features.pedidos.dto.*;
import com.dompet.api.features.pedidos.repo.PedidosRepository;
import com.dompet.api.features.produtos.domain.Produtos;
import com.dompet.api.features.produtos.repo.ProdutosRepository;
import com.dompet.api.features.usuarios.domain.Usuarios;
import com.dompet.api.shared.endereco.EnderecoDto;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Regras de negócio de pedidos (checkout e gestão de status).
 * - Valida estoque no momento do checkout e registra snapshot de preços
 * - Fecha o carrinho e limpa itens após checkout
 * - Implementa transições de status com reestoque em cancelamentos iniciais
 */
@Service
public class PedidoService {
    private final PedidosRepository pedidosRepo;
    private final ProdutosRepository produtosRepo;
    private final UsuariosRepository usuariosRepo;
    private final CarrinhoRepository carrinhoRepo;

    public PedidoService(PedidosRepository pedidosRepo, ProdutosRepository produtosRepo,
                         UsuariosRepository usuariosRepo, CarrinhoRepository carrinhoRepo) {
        this.pedidosRepo = pedidosRepo;
        this.produtosRepo = produtosRepo;
        this.usuariosRepo = usuariosRepo;
        this.carrinhoRepo = carrinhoRepo;
    }

    /**
     * Executa o checkout do carrinho aberto do usuário.
     * Verifica estoque (ativo/quantidade), cria pedido com snapshot de preços, debita estoque,
     * fecha o carrinho e retorna o DTO do pedido.
     */
    @Transactional
    public PedidoResponseDto checkout(String email, CheckoutDto dto) {
        Carrinho carrinho = carrinhoRepo.findByUsuarioEmailAndStatus(email, CartStatus.ABERTO)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho vazio"));
        if (carrinho.getItens().isEmpty()) throw new IllegalArgumentException("Carrinho vazio");

    // Recarregar e validar estoque
        List<String> semEstoque = new ArrayList<>();
        Map<Long, Produtos> produtosById = new HashMap<>();
        carrinho.getItens().forEach(item -> {
            Produtos p = produtosRepo.findById(item.getProduto().getId())
                    .orElseThrow(() -> new NotFoundException("Produto não encontrado"));
            produtosById.put(p.getId(), p);
            if (Boolean.FALSE.equals(p.getAtivo()) || p.getEstoque() == null || p.getEstoque() < item.getQuantidade()) {
                semEstoque.add(p.getNome());
            }
        });
        if (!semEstoque.isEmpty()) {
            throw new InsufficientStockException("Estoque insuficiente para alguns itens", semEstoque);
        }

    // Criar pedido
        Usuarios usuario = usuariosRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuário"));
        Pedidos pedido = new Pedidos();
        pedido.setUsuario(usuario);
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
    pedido.setEnderecoEntrega(dto.enderecoEntrega().toEntity());

        List<ItemPedido> itensPedido = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (var item : carrinho.getItens()) {
            Produtos p = produtosById.get(item.getProduto().getId());
            ItemPedido ip = new ItemPedido();
            ip.setPedido(pedido);
            ip.setProduto(p);
            ip.setNomeProduto(p.getNome()); // snapshot do nome
            ip.setQuantidade(item.getQuantidade());
            ip.setPrecoUnitario(p.getPreco()); // snapshot do preço no momento do pedido
            ip.calcularSubtotal();
            itensPedido.add(ip);
            total = total.add(ip.getSubtotal() == null ? BigDecimal.ZERO : ip.getSubtotal());

            // Decrementar estoque
            p.setEstoque(p.getEstoque() - item.getQuantidade());
        }
    pedido.setItens(itensPedido);
    // Persiste o total calculado no próprio pedido
    pedido.setTotal(total);

    // Fechar carrinho
        carrinho.setStatus(CartStatus.FECHADO);
        carrinho.getItens().clear();

        pedidosRepo.save(pedido);
        // produtosRepo.saveAll already managed by transactional dirty checking

        return toDto(pedido);
    }

    /** Busca pedido por ID; se não for ADMIN, valida que o dono é o solicitante. */
    @Transactional(readOnly = true)
    public PedidoResponseDto getById(String email, Long id, boolean isAdmin) {
        var pedido = pedidosRepo.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
        if (!isAdmin && (pedido.getUsuario() == null || !email.equals(pedido.getUsuario().getEmail()))) {
            throw new ForbiddenException("Acesso negado a este pedido");
        }
        return toDto(pedido);
    }

    /** Lista paginada dos pedidos do usuário autenticado. */
    @Transactional(readOnly = true)
    public Page<PedidoResponseDto> listMine(String email, Pageable p) {
        return pedidosRepo.findByUsuarioEmail(email, p).map(this::toDto);
    }

    /** Atualiza status de pedido como ADMIN; restaura estoque ao cancelar antes de envio. */
    @Transactional
    public void updateStatusAsAdmin(Long pedidoId, StatusPedido novo) {
        var pedido = pedidosRepo.findById(pedidoId).orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
        StatusPedido atual = pedido.getStatus();
        if (!isTransitionAllowed(atual, novo)) {
            throw new IllegalArgumentException("Transição de status inválida: " + atual + " -> " + novo);
        }
        // Restock se cancelar antes de ENVIADO
        if (novo == StatusPedido.CANCELADO && (atual == StatusPedido.AGUARDANDO_PAGAMENTO || atual == StatusPedido.PAGO)) {
            if (pedido.getItens() != null) {
                for (var item : pedido.getItens()) {
                    Produtos p = item.getProduto();
                    if (p != null && item.getQuantidade() != null) {
                        p.setEstoque(p.getEstoque() + item.getQuantidade());
                    }
                }
            }
        }
        pedido.setStatus(novo);
    }

    /** Regras de transição de status. */
    private boolean isTransitionAllowed(StatusPedido atual, StatusPedido novo) {
        return switch (atual) {
            case AGUARDANDO_PAGAMENTO -> (novo == StatusPedido.PAGO || novo == StatusPedido.CANCELADO);
            case PAGO -> (novo == StatusPedido.ENVIADO || novo == StatusPedido.CANCELADO);
            case ENVIADO -> (novo == StatusPedido.ENTREGUE);
            case ENTREGUE, CANCELADO -> false;
        };
    }

    /** Mapeia entidade de pedido para DTO de resposta. */
    private PedidoResponseDto toDto(Pedidos p) {
    var items = new ArrayList<PedidoResponseDto.ItemDto>();
        if (p.getItens() != null) {
            for (var item : p.getItens()) {
        // subtotal é @Transient; pode vir nulo após carregar do banco.
        BigDecimal sub = item.getSubtotal();
        if (sub == null && item.getPrecoUnitario() != null && item.getQuantidade() != null) {
            sub = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
                .setScale(2, RoundingMode.HALF_UP);
        }
                items.add(new PedidoResponseDto.ItemDto(
                        item.getProduto() != null ? item.getProduto().getId() : null,
                        item.getProduto() != null ? item.getProduto().getNome() : null,
                        item.getPrecoUnitario(),
                        item.getQuantidade(),
            sub
                ));
            }
        }
    // Usa o total persistido quando disponível; caso contrário, soma os subtotais mapeados
    BigDecimal total = p.getTotal();
    if (total == null) {
        total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (var it : items) { if (it.subtotal() != null) total = total.add(it.subtotal()); }
    }

    Date createdAt = p.getCreatedAt();

    return new PedidoResponseDto(p.getId(),
                p.getStatus() != null ? p.getStatus().name() : null,
        EnderecoDto.from(p.getEnderecoEntrega()),
                items,
                total,
                createdAt);
    }
}
