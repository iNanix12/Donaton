package com.donaton.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Filtro global que se ejecuta en CADA request que pase por el Gateway.
 *
 * Responsabilidades:
 * 1. Genera un X-Correlation-ID único por request para trazabilidad distribuida.
 * 2. Loguea método, path y tiempo de respuesta.
 * 3. Propaga el correlation ID en la respuesta para que el frontend pueda
 *    reportarlo en caso de error.
 *
 * Al implementar Ordered con HIGHEST_PRECEDENCE, este filtro se ejecuta
 * antes que cualquier filtro de ruta específico.
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Genera o propaga el Correlation ID
        String correlationId = request.getHeaders().containsKey("X-Correlation-ID")
                ? request.getHeaders().getFirst("X-Correlation-ID")
                : UUID.randomUUID().toString();

        long startTime = System.currentTimeMillis();

        log.info("[REQUEST]  {} {} | correlationId={} | remoteAddr={}",
                request.getMethod(),
                request.getURI().getPath(),
                correlationId,
                request.getRemoteAddress() != null
                        ? request.getRemoteAddress().getAddress().getHostAddress()
                        : "unknown");

        // Mutamos el request para agregar el correlation ID en headers internos
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Correlation-ID", correlationId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("[RESPONSE] {} {} | status={} | duration={}ms | correlationId={}",
                            request.getMethod(),
                            request.getURI().getPath(),
                            exchange.getResponse().getStatusCode(),
                            duration,
                            correlationId);

                    // Expone el correlation ID en la respuesta para debugging del cliente
                    exchange.getResponse().getHeaders()
                            .add("X-Correlation-ID", correlationId);
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}