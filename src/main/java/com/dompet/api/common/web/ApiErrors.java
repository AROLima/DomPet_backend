package com.dompet.api.common.web;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import com.dompet.api.features.carrinho.errors.*;
import com.dompet.api.common.errors.*;

import java.util.List;
// removed manual decorate utilities (now in ErrorResponseFactory)

/**
 * Mapeia exceções para ProblemDetail (RFC 7807) usando {@link ErrorResponseFactory}.
 * Consolida handlers anteriores (GlobalExceptionHandler deprecado) em um único ponto.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiErrors {
    /** Estrutura para representar violações de campos em validações. */
    record FieldViolation(String field, String message) {}

    /**
     * Handler centralizado de exceções que converte exceções Java em ProblemDetail (RFC 7807).
     * Objetivo didático: mostrar como mapear exceções para códigos HTTP sem espalhar try/catch pelos controllers.
     */
    // Observação: este bean centraliza o mapeamento de exceções e é prioridade sobre handlers locais.

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        // Força status 404 mesmo quando outra infra tenta transformar a exceção em 500
        ProblemDetail pd = ErrorResponseFactory.create(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req, ex);
        // Garantir coerência caso algum decorator tenha sobrescrito
        if (pd.getStatus() != HttpStatus.NOT_FOUND.value()) {
            pd.setProperty("status", HttpStatus.NOT_FOUND.value());
        }
        return pd;
    }

    /** 400 com lista de violações de campo para @Valid em @RequestBody. */
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponseFactory.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponseFactory.FieldViolation(fe.getField(), resolveMessage(fe)))
                .toList();
        return ErrorResponseFactory.validation(req, ex, violations, "Validation failed");
    }

    /** 400 para violações de constraints em parâmetros (@RequestParam/@PathVariable). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<ErrorResponseFactory.FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(cv -> new ErrorResponseFactory.FieldViolation(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();
        return ErrorResponseFactory.validation(req, ex, violations, "Validation failed");
    }

    /** 400 genérico para argumentos inválidos. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req, ex);
    }

    /** 403 para acesso negado. */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to access this resource", req, ex);
    }

    // Carrinho-specific exceptions
    @ExceptionHandler({ CarrinhoNaoEncontradoException.class, ProdutoNaoEncontradoException.class })
    public ProblemDetail handleCartNotFound(RuntimeException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req, ex);
    }

    /** 400 quando o delta é inválido (<= 0 na criação, negativo sem item, etc.). */
    @ExceptionHandler(AlteracaoQuantidadeInvalidaException.class)
    public ProblemDetail handleInvalidDelta(AlteracaoQuantidadeInvalidaException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.BAD_REQUEST, "Invalid quantity change", ex.getMessage(), req, ex);
    }

    /** 409 quando a quantidade solicitada excede o estoque disponível. */
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ProblemDetail handleStockConflict(EstoqueInsuficienteException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.CONFLICT, "Conflict", "Violação de integridade de dados", req, ex);
    }

    @ExceptionHandler({ NotFoundException.class })
    public ProblemDetail handleGenericNotFound(NotFoundException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req, ex);
    }

    @ExceptionHandler({ ForbiddenException.class })
    public ProblemDetail handleGenericForbidden(ForbiddenException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req, ex);
    }

    @ExceptionHandler({ InsufficientStockException.class })
    public ProblemDetail handleGenericStock(InsufficientStockException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req, ex);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), req, ex);
    }

    private String resolveMessage(FieldError fe) { return fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid value"; }
}
