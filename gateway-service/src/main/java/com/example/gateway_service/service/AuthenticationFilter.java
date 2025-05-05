package com.example.api_gateway.service;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator validator;
    private final JwtUtils jwtUtils;

    public AuthenticationFilter(RouterValidator validator, JwtUtils jwtUtils) {
        this.validator = validator;
        this.jwtUtils = jwtUtils;
    }



    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            var request = exchange.getRequest();
            var path = request.getURI().getPath();

            ServerHttpRequest serverHttpRequest = null;
            // Es un open endpoint? --> Endpoint que no requiere autentificación
            if (validator.isSecured.test(request)) {
                if (authMissing(request)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                // Comprobamos el token sin Bearer

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                } else {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                if (jwtUtils.isExpired(authHeader)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                // Mutamos la siguiente petición y la enrutamos al servicio correspondiente
                serverHttpRequest = exchange.getRequest()
                        .mutate()
                        .header("userIdRequest", jwtUtils.extractUserId(authHeader).toString())
                        .build();
            }

            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return null;
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    public static class Config{}
}
