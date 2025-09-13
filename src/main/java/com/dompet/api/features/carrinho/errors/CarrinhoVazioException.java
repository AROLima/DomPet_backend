package com.dompet.api.features.carrinho.errors;

public class CarrinhoVazioException extends RuntimeException {
    public CarrinhoVazioException() { super("Carrinho vazio"); }
    public CarrinhoVazioException(String message) { super(message); }
}
