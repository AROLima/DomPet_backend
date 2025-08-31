package com.dompet.api.common.errors;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.time.OffsetDateTime;
import java.util.*;


@RestControllerAdvice
public class GlobalExceptionHandler {
    record ValidationError(String field, String message) {}
    // Estrutura uniforme usada nas respostas de erro para facilitar o consumo pelo cliente
    record ErrorBody(String timestamp, int status, String error, String message, String path, String code, List<ValidationError> errors) {}

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(NotFoundException ex, org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorBody(OffsetDateTime.now().toString(), 404, "Not Found", ex.getMessage(), req.getDescription(false), "NOT_FOUND", null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorBody> handleForbidden(ForbiddenException ex, org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorBody(OffsetDateTime.now().toString(), 403, "Forbidden", ex.getMessage(), req.getDescription(false), "FORBIDDEN", null));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorBody> handleStock(InsufficientStockException ex, org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorBody(OffsetDateTime.now().toString(), 409, "Conflict", ex.getMessage(), req.getDescription(false), "INSUFFICIENT_STOCK", null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorBody> handleIllegal(IllegalArgumentException ex, org.springframework.web.context.request.WebRequest req) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorBody(OffsetDateTime.now().toString(), 400, "Bad Request", ex.getMessage(), req.getDescription(false), "BAD_REQUEST", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex, org.springframework.web.context.request.WebRequest req) {
    List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage()))
        .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorBody(OffsetDateTime.now().toString(), 400, "Bad Request", "Validação falhou", req.getDescription(false), "VALIDATION_ERROR", errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorBody> handleDataIntegrity(DataIntegrityViolationException ex, org.springframework.web.context.request.WebRequest req) {
    // Provável SKU duplicado
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorBody(OffsetDateTime.now().toString(), 409, "Conflict", "Violação de integridade de dados", req.getDescription(false), "DATA_INTEGRITY", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleOther(Exception ex, org.springframework.web.context.request.WebRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorBody(OffsetDateTime.now().toString(), 500, "Internal Server Error", ex.getMessage(), req.getDescription(false), "ERROR", null));
    }
}
