package com.dompet.api.common.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro para permitir SPA (Flutter Web) rodar em rotas client-side.
 * Estratégia: se a requisição aparenta ser navegação de browser (Accept HTML)
 * e não corresponde a endpoint REST (começa por /api? não usamos prefixo) ou recurso estático
 * (possui ponto na última parte), encaminha para /index.html que o Flutter gerou.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class SpaFallbackFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String accept = req.getHeader("Accept");

        boolean isApi = uri.startsWith("/auth") || uri.startsWith("/produtos") || uri.startsWith("/cart") || uri.startsWith("/carrinho") || uri.startsWith("/pedidos") || uri.startsWith("/swagger") || uri.startsWith("/v3");
        boolean hasExtension = uri.matches(".*\\.[a-zA-Z0-9]{2,6}$");
        boolean wantsHtml = accept != null && accept.contains("text/html");

        if (!isApi && !hasExtension && wantsHtml) {
            // Forward para index.html (servido do resources/static)
            request.getRequestDispatcher("/index.html").forward(req, res);
            return;
        }
        chain.doFilter(request, response);
    }
}
