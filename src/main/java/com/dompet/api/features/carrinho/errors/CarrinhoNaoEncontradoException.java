package com.dompet.api.features.carrinho.errors;

/** Exceção lançada quando o carrinho requisitado não existe para o usuário. */
public class CarrinhoNaoEncontradoException extends RuntimeException {
    public CarrinhoNaoEncontradoException(String message) { super(message); }
}
