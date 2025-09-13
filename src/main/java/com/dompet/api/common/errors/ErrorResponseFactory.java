package com.dompet.api.common.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Factory central para criar ProblemDetail enriquecido mantendo campos legados
 * (timestamp, status, error, path, code, errors) para compatibilidade.
 */
public final class ErrorResponseFactory {

    private ErrorResponseFactory() {}

    /** Campo de erro de validação granular. */
    public record FieldViolation(String field, String message) {}

    public static ProblemDetail create(HttpStatus status, String title, String detail,
                                       HttpServletRequest request, Throwable ex) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        if (title != null) pd.setTitle(title); else pd.setTitle(status.getReasonPhrase());
        if (detail != null) pd.setDetail(detail);
        enrich(pd, status, request, ex);
        return pd;
    }

    public static ProblemDetail validation(HttpServletRequest request, Throwable ex,
                                           List<FieldViolation> violations, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(detail != null ? detail : "One or more fields are invalid");
        if (violations != null) pd.setProperty("errors", violations);
        enrich(pd, HttpStatus.BAD_REQUEST, request, ex);
        return pd;
    }

    private static void enrich(ProblemDetail pd, HttpStatus status, HttpServletRequest request, Throwable ex) {
        pd.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString());
        pd.setProperty("status", status.value());
        pd.setProperty("error", status.getReasonPhrase());
        if (request != null) pd.setProperty("path", request.getRequestURI());
        if (ex != null) pd.setProperty("code", ex.getClass().getSimpleName());
    }
}