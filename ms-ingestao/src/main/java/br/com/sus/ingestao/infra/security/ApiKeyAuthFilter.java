package br.com.sus.ingestao.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * API Key filter that authenticates requests using X-API-KEY header.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-KEY";

    private final String expectedApiKey;

    public ApiKeyAuthFilter(@Value("${sus.security.api-token:}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Only enforce API key for protected integration endpoints
        boolean isProtectedPath = path.startsWith("/api/v1/integracao/") || 
                                   path.equals("/api/v1/integracao") ||
                                   ("POST".equalsIgnoreCase(request.getMethod()) && path.startsWith("/api/v1/integracao"));

        if (!isProtectedPath) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(API_KEY_HEADER);
        if (header == null || header.isBlank() || expectedApiKey == null || expectedApiKey.isBlank() || !Objects.equals(header, expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Set authentication with ROLE_INTEGRADOR
        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "integrador", null, List.of(new SimpleGrantedAuthority("ROLE_INTEGRADOR"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
