package com.seguros.polizas.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Seguridad mínima: exige el header x-api-key en todas las peticiones. */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    private final String apiKey;

    public ApiKeyFilter(@Value("${app.security.api-key}") String apiKey) { this.apiKey = apiKey; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (!apiKey.equals(req.getHeader("x-api-key"))) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"x-api-key inválida o ausente\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}
