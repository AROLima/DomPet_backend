package com.dompet.api.features.carrinho.errors;

/** Exceção lançada quando o produto especificado não é encontrado. */
public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(String message) { super(message); }
}
