package com.dompet.api.common.web;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import com.dompet.api.features.carrinho.errors.*;

import java.util.List;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

/**
 * Mapeia exceções para respostas ProblemDetail padronizadas (RFC 7807).
 * Inclui validações, 404, 403 e erros específicos do carrinho.
 */
@RestControllerAdvice
public class ApiErrors {

    record FieldViolation(String field, String message) {}

    /** 404 para entidades não encontradas (ex.: Produto). */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setDetail(ex.getMessage());
        decorate(pd, HttpStatus.NOT_FOUND, req, ex);
        return pd;
    }

    /** 400 com lista de violações de campo para @Valid em @RequestBody. */
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new FieldViolation(fe.getField(), resolveMessage(fe)))
                .collect(Collectors.toList());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("One or more fields are invalid");
        pd.setProperty("errors", violations);
        decorate(pd, HttpStatus.BAD_REQUEST, req, ex);
        return pd;
    }

    /** 400 para violações de constraints em parâmetros (@RequestParam/@PathVariable). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(cv -> new FieldViolation(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("One or more constraints were violated");
        pd.setProperty("errors", violations);
        decorate(pd, HttpStatus.BAD_REQUEST, req, ex);
        return pd;
    }

    /** 400 genérico para argumentos inválidos. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setDetail(ex.getMessage());
        decorate(pd, HttpStatus.BAD_REQUEST, req, ex);
        return pd;
    }

    /** 403 para acesso negado. */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Forbidden");
        pd.setDetail("You do not have permission to access this resource");
        decorate(pd, HttpStatus.FORBIDDEN, req, ex);
        return pd;
    }

    private String resolveMessage(FieldError fe) {
        return fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid value";
    }

    // Carrinho-specific exceptions
    @ExceptionHandler({ CarrinhoNaoEncontradoException.class, ProdutoNaoEncontradoException.class })
    public ProblemDetail handleCartNotFound(RuntimeException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setDetail(ex.getMessage());
        decorate(pd, HttpStatus.NOT_FOUND, req, ex);
        return pd;
    }

    /** 400 quando o delta é inválido (<= 0 na criação, negativo sem item, etc.). */
    @ExceptionHandler(AlteracaoQuantidadeInvalidaException.class)
    public ProblemDetail handleInvalidDelta(AlteracaoQuantidadeInvalidaException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid quantity change");
        pd.setDetail(ex.getMessage());
        decorate(pd, HttpStatus.BAD_REQUEST, req, ex);
        return pd;
    }

    /** 409 quando a quantidade solicitada excede o estoque disponível. */
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ProblemDetail handleStockConflict(EstoqueInsuficienteException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        decorate(pd, HttpStatus.CONFLICT, req, ex);
        return pd;
    }

    private void decorate(ProblemDetail pd, HttpStatus status, HttpServletRequest req, Exception ex) {
        pd.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString());
        pd.setProperty("status", status.value());
        pd.setProperty("error", status.getReasonPhrase());
        pd.setProperty("path", req != null ? req.getRequestURI() : null);
        pd.setProperty("code", ex.getClass().getSimpleName());
    }
}
