package com.ogc_prototype.ogc.config;

import tools.jackson.databind.ObjectMapper;
import com.ogc_prototype.ogc.model.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public RoleInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        // Busca @RequiresRole en el método, luego en la clase
        RequiresRole annotation = method.getMethodAnnotation(RequiresRole.class);
        if (annotation == null) {
            annotation = method.getBeanType().getAnnotation(RequiresRole.class);
        }

        if (annotation == null) {
            return true; 
        }

        Role userRole = (Role) request.getAttribute("role");

        if (userRole == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Autenticación requerida");
            return false;
        }

        if (!Arrays.asList(annotation.value()).contains(userRole)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permisos para este recurso");
            return false;
        }

        return true;
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("error", message)));
    }
}

