package com.dompet.api.features.carrinho.errors;

/** Exceção lançada quando não existe estoque suficiente para a operação solicitada. */
public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String message) { super(message); }
}
