package com.ogc_prototype.ogc.config;

import com.ogc_prototype.ogc.model.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (!jwtUtils.isTokenValid(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token inválido o expirado");
                return;
            }

            // Almacenamos los datos del usuario en el request para que el interceptor los lea
            request.setAttribute("userId", jwtUtils.extractUserId(token));
            request.setAttribute("username", jwtUtils.extractUsername(token));
            request.setAttribute("role", jwtUtils.extractRole(token));
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
