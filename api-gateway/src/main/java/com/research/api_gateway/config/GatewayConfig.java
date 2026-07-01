package com.research.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        log.info("Initializing API Gateway routes...");

        return builder.routes()

                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "api-gateway"))
                        .uri("lb://auth-service"))

                .route("paper-service", r -> r
                        .path("/papers/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "api-gateway"))
                        .uri("lb://paper-service"))

                .route("analysis-service", r -> r
                        .path("/analysis/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "api-gateway"))
                        .uri("lb://ai-analysis-service"))

                .route("insight-service", r -> r
                        .path("/insights/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "api-gateway"))
                        .uri("lb://insight-service"))

                .build();
    }
}