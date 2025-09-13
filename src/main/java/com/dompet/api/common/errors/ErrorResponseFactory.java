package com.dompet.api.common.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import com.dompet.api.features.carrinho.errors.AlteracaoQuantidadeInvalidaException;
import com.dompet.api.features.carrinho.errors.CarrinhoNaoEncontradoException;
import com.dompet.api.features.carrinho.errors.EstoqueInsuficienteException;
import com.dompet.api.features.carrinho.errors.ProdutoNaoEncontradoException;

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
        applyType(pd, ex, status, false);
        enrich(pd, status, request, ex);
        return pd;
    }

    public static ProblemDetail validation(HttpServletRequest request, Throwable ex,
                                           List<FieldViolation> violations, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(detail != null ? detail : "One or more fields are invalid");
        if (violations != null) pd.setProperty("errors", violations);
        // force validation type
        pd.setProperty("type", baseType() + "/validation-error");
        enrich(pd, HttpStatus.BAD_REQUEST, request, ex);
        return pd;
    }

    private static void enrich(ProblemDetail pd, HttpStatus status, HttpServletRequest request, Throwable ex) {
        pd.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString());
        pd.setProperty("status", status.value());
        pd.setProperty("error", status.getReasonPhrase());
        if (request != null) pd.setProperty("path", request.getRequestURI());
        if (ex != null) pd.setProperty("code", ex.getClass().getSimpleName());
        // RFC 7807 'instance': identificador único (trace) desta ocorrência
        pd.setProperty("instance", "/errors/" + UUID.randomUUID());
    }

    private static void applyType(ProblemDetail pd, Throwable ex, HttpStatus status, boolean override) {
    var props = pd.getProperties();
    if (props != null && props.containsKey("type") && !override) return;
        String type;
        if (ex == null) {
            type = baseType() + "/generic-error";
        } else {
            type = TYPE_MAP.entrySet().stream()
                    .filter(e -> e.getKey().isAssignableFrom(ex.getClass()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(baseType() + "/" + toKebab(ex.getClass().getSimpleName()));
        }
        pd.setProperty("type", type);
    }

    private static String baseType() { return "https://api.dompet.local/problem"; }

    private static String toKebab(String simple) {
        return simple
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .replaceAll("[^a-zA-Z0-9-]", "-")
                .toLowerCase();
    }

    // Ordem importa: classes mais específicas primeiro
    private static final Map<Class<?>, String> TYPE_MAP = Map.ofEntries(
            Map.entry(InsufficientStockException.class, baseType() + "/insufficient-stock"),
            Map.entry(EstoqueInsuficienteException.class, baseType() + "/insufficient-stock"),
            Map.entry(AlteracaoQuantidadeInvalidaException.class, baseType() + "/invalid-quantity-change"),
            Map.entry(CarrinhoNaoEncontradoException.class, baseType() + "/cart-not-found"),
            Map.entry(ProdutoNaoEncontradoException.class, baseType() + "/product-not-found"),
            Map.entry(ForbiddenException.class, baseType() + "/forbidden"),
        // Canonical mapping for generic JPA entity not found
        Map.entry(jakarta.persistence.EntityNotFoundException.class, baseType() + "/not-found"),
            Map.entry(NotFoundException.class, baseType() + "/not-found")
    );
}