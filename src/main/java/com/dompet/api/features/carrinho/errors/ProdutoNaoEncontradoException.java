package com.dompet.api.features.carrinho.errors;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(String message) { super(message); }
}
