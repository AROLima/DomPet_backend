package com.dompet.api.features.carrinho.errors;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String message) { super(message); }
}
