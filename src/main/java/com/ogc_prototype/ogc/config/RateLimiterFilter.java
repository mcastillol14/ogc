package com.ogc_prototype.ogc.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


@Slf4j
@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    @Value("${rate.limit.capacity:60}")
    private int capacity;

    @Value("${rate.limit.window-seconds:60}")
    private long windowSeconds;

    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> windowMap =
            new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    public RateLimiterFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String ip = resolveClientIp(request);

        if (!isAllowed(ip)) {
            log.warn("[RATE-LIMIT] IP bloqueada: {} — superó el límite de {} req/{}s", ip, capacity,
                    windowSeconds);
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(
                    Map.of("error", "Demasiadas peticiones. Intenta nuevamente en un momento.")));
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAllowed(String ip) {
        long now = System.currentTimeMillis();
        long windowMs = windowSeconds * 1000L;

        ConcurrentLinkedDeque<Long> timestamps =
                windowMap.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

        timestamps.removeIf(t -> now - t > windowMs);

        if (timestamps.size() >= capacity) {
            return false;
        }

        timestamps.add(now);
        return true;
    }


    @Scheduled(fixedDelayString = "${rate.limit.cleanup-delay-ms:120000}")
    void cleanStaleEntries() {
        long now = System.currentTimeMillis();
        long windowMs = windowSeconds * 1000L;
        int removed = 0;

        for (Map.Entry<String, ConcurrentLinkedDeque<Long>> entry : windowMap.entrySet()) {
            entry.getValue().removeIf(t -> now - t > windowMs);
            if (entry.getValue().isEmpty()) {
                windowMap.remove(entry.getKey());
                removed++;
            }
        }

        if (removed > 0) {
            log.debug("[RATE-LIMIT] Eliminadas {} entradas inactivas.", removed);
        }
    }


    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String ip = xff.split(",")[0].trim();
            // Acepta solo caracteres válidos de IPv4/IPv6
            if (ip.matches("[0-9a-fA-F:.]{2,45}")) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
