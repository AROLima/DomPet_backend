package com.dompet.api.features.carrinho.errors;

/** Exceção lançada quando uma alteração de quantidade é inválida (ex: negativa). */
public class AlteracaoQuantidadeInvalidaException extends RuntimeException {
    public AlteracaoQuantidadeInvalidaException(String message) { super(message); }
}
