package com.research.api_gateway.filter;

import com.research.api_gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        String requestId = UUID.randomUUID().toString();

        log.info("Incoming Request => ID={}, Method={}, Path={}",
                requestId, method, path);

        // Public APIs
        if (path.startsWith("/auth/login") ||
                path.startsWith("/auth/register")) {

            log.info("Public endpoint accessed => ID={}, Path={}", requestId, path);
            return chain.filter(exchange)
                    .doFinally(signal -> logResponse(exchange, requestId, startTime));
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            log.warn("Missing/invalid Authorization header => ID={}, Path={}", requestId, path);

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {

            log.warn("Invalid JWT token => ID={}, Path={}", requestId, path);

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String email = jwtUtil.getUsername(token);
        Long userId = jwtUtil.getUserId(token);

        log.info("Authenticated user => ID={}, userId={}, email={}",
                requestId, userId, email);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(
                        exchange.getRequest()
                                .mutate()
                                .header("X-User-Email", email)
                                .header("X-User-Id", String.valueOf(userId))
                                .header("X-Request-Id", requestId)
                                .build())
                .build();

        return chain.filter(mutatedExchange)
                .doFinally(signal -> logResponse(exchange, requestId, startTime));
    }

    private void logResponse(ServerWebExchange exchange, String requestId, long startTime) {
        long timeTaken = System.currentTimeMillis() - startTime;

        log.info("Response completed => ID={}, Status={}, Time={}ms",
                requestId,
                exchange.getResponse().getStatusCode(),
                timeTaken);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}