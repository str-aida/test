package com.Trabajo_Final_Beltran.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

        private Bucket crearBucket(String uri) {
        if (uri.contains("/auth/login")
                || uri.contains("/auth/register")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(
                            5,
                            Refill.greedy(5, Duration.ofMinutes(1))
                    ))
                    .build();
        }
        if (uri.contains("/auth/restablecer-password")
                || uri.contains("/auth/solicitar-recuperacion")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(
                            5,
                            Refill.greedy(5, Duration.ofMinutes(15))
                    ))
                    .build();
        }
        if (uri.contains("/api/pagos/webhook")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(
                            30,
                            Refill.greedy(30, Duration.ofMinutes(1))
                    ))
                    .build();
        }
        if (uri.contains("/pedidos")
                && uri.equals("/pedidos")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(
                            10,
                            Refill.greedy(10, Duration.ofMinutes(1))
                    ))
                    .build();
        }
        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        20,
                        Refill.greedy(20, Duration.ofMinutes(1))
                ))
                .build();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();

        String identificador = obtenerIdentificador(request);

        String key = identificador + ":" + uri;

        Bucket bucket = buckets.computeIfAbsent(
                key,
                k -> crearBucket(uri)
        );

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            System.out.println(
                "RATE LIMIT excedido - identificador: "
                + identificador
                + " uri: "
                + uri
            );
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\": \"Demasiadas solicitudes. Intentá más tarde.\"}"
            );
        }
    }

    private String obtenerIdentificador(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return request.getUserPrincipal().getName();
        }


        return request.getRemoteAddr();
    }
}
