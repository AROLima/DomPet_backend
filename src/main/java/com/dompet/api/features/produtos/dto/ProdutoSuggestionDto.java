package com.dompet.api.features.produtos.dto;

/**
 * DTO enxuto para autocomplete de produtos.
 * Retorna apenas campos necessários para sugestão rápida.
 */
public record ProdutoSuggestionDto(Long id, String nome, String imagemUrl, String sku) {
}
