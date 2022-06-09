package com.apigateway.gateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthGlobaleFilter implements GlobalFilter {
    final Logger logger = LoggerFactory.getLogger(AuthGlobaleFilter.class);
    final RestTemplate restTemplate;

    public AuthGlobaleFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Mono filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().toString();
        boolean header = exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
        boolean authPathIsFound = requestPath.contains("/api/auth/");

        if (!authPathIsFound) {
            if (!header) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();            }
        }
        //


        return chain.filter(exchange);
    }
}