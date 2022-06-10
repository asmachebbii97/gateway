package com.apigateway.gateway.config;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;

@Component
public class GetJobFilter implements  GatewayFilterFactory{

	@Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
        	
        	boolean status = exchange.getRequest().getMethod().equals(HttpMethod.GET);
        	String requestPath = exchange.getRequest().getPath().toString();
        	boolean authPathIsFound = requestPath.contains("/JobOffers/");
            if(status && authPathIsFound )
            {
            	exchange.getResponse().setStatusCode(HttpStatus.ACCEPTED);
            }
            else {
            	 exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                 return exchange.getResponse().setComplete();
            }
            
            return chain.filter(exchange);
        };
    }
    

}
