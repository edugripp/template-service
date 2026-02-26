package com.example.template.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

@Component
@ConditionalOnProperty(name = "metrics.aggregated.enabled", havingValue = "true", matchIfMissing = false)
public class AggregatedLoggingFilter extends OncePerRequestFilter {

    private ConcurrentMap<String, EndpointStats> statsMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Regex is too slow for 1 CPU at P99, manual zero-allocation parser
        String requestURI = request.getRequestURI();
        int lastSlash = requestURI.lastIndexOf('/');
        if (lastSlash != -1 && lastSlash < requestURI.length() - 1) {
            String afterSlash = requestURI.substring(lastSlash + 1);
            boolean isNumeric = true;
            for (int i = 0; i < afterSlash.length(); i++) {
                if (!Character.isDigit(afterSlash.charAt(i))) {
                    isNumeric = false;
                    break;
                }
            }
            if (isNumeric) {
                requestURI = requestURI.substring(0, lastSlash) + "/{id}";
            }
        }

        // Pula endpoints irrelevantes para métricas de stress
        if (requestURI.startsWith("/v3/api-docs") || requestURI.startsWith("/swagger-ui")
                || requestURI.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);

        int status = response.getStatus();

        EndpointStats stats = statsMap.computeIfAbsent(request.getMethod() + " " + requestURI,
                k -> new EndpointStats());

        if (status >= 200 && status < 300) {
            stats.success().increment();
        } else if (status >= 400) {
            stats.errors().increment();
        }
    }

    public ConcurrentMap<String, EndpointStats> extractAndReset() {
        ConcurrentMap<String, EndpointStats> snapshot = statsMap;
        statsMap = new ConcurrentHashMap<>();
        return snapshot;
    }

    public record EndpointStats(LongAdder success, LongAdder errors) {
        public EndpointStats() {
            this(new LongAdder(), new LongAdder());
        }
    }
}
