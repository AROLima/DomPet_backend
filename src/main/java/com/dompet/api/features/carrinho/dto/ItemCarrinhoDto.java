// ItemCarrinhoDto.java
// DTO simples que representa uma linha do carrinho enviada ao cliente.
package com.dompet.api.features.carrinho.dto;

import java.math.BigDecimal;

/** Item no DTO do carrinho resumido. */
public record ItemCarrinhoDto(
    Long produtoId,
    String nome,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal lineTotal
) {}
