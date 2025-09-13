package com.dompet.api.features.carrinho.mapper;

import com.dompet.api.features.carrinho.domain.Carrinho;
import com.dompet.api.features.carrinho.domain.ItemCarrinho;
import com.dompet.api.features.carrinho.dto.CarrinhoDto;
import com.dompet.api.features.carrinho.dto.ItemCarrinhoDto;
import com.dompet.api.features.carrinho.dto.CartResponseDto;

import java.util.List;

/**
 * Mapeia entidades de carrinho/itens para os diferentes DTOs públicos.
 * Futuras evoluções (ex: campos promocionais) centralizarão aqui.
 */
public final class CartMapper {
    private CartMapper() {}

    public static CartResponseDto toCartResponseDto(Carrinho carrinho) {
        List<CartResponseDto.ItemDto> itens = carrinho.getItens().stream()
                .map(CartMapper::toCartItemDto)
                .toList();
        return new CartResponseDto(carrinho.getId(), itens, carrinho.getTotal());
    }

    private static CartResponseDto.ItemDto toCartItemDto(ItemCarrinho item) {
        return new CartResponseDto.ItemDto(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getProduto().getPreco(),
                item.getQuantidade(),
                item.getSubtotal()
        );
    }

    public static CarrinhoDto toCarrinhoDto(Carrinho carrinho) {
        List<ItemCarrinhoDto> itens = carrinho.getItens().stream()
                .map(CartMapper::toItemCarrinhoDto)
                .toList();
        return new CarrinhoDto(carrinho.getId(), itens, carrinho.getTotal());
    }

    private static ItemCarrinhoDto toItemCarrinhoDto(ItemCarrinho item) {
        return new ItemCarrinhoDto(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getProduto().getPreco(),
                item.getQuantidade(),
                item.getSubtotal()
        );
    }
}