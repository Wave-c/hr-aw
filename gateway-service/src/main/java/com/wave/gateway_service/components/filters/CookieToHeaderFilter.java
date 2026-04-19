package com.wave.gateway_service.components.filters;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CookieToHeaderFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpCookie accessTokenCookie = exchange.getRequest()
            .getCookies()
            .getFirst("access_token");

        if (accessTokenCookie != null) {
            ServerHttpRequest requestWithHeader = exchange.getRequest()
                .mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                    accessTokenCookie.getValue())
                .build();

            return chain
                .filter(exchange.mutate()
                    .request(requestWithHeader).build());
        }

        return chain.filter(exchange);
    }

}
