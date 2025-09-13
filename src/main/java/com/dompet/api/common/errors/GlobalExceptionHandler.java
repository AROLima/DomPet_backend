package com.dompet.api.common.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @deprecated Substituído por {@link com.dompet.api.common.web.ApiErrors}. Mantido por compatibilidade temporária.
 * Agora delega para {@link ErrorResponseFactory} e retorna ProblemDetail.
 */
@Deprecated
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req, ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req, ex);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleStock(InsufficientStockException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegal(IllegalArgumentException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponseFactory.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponseFactory.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ErrorResponseFactory.validation(req, ex, violations, "Validação falhou");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.CONFLICT, "Conflict", "Violação de integridade de dados", req, ex);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex, HttpServletRequest req) {
        return ErrorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), req, ex);
    }
}
