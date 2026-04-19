package com.wave.user_service.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class JwksController {
    private final JWKSet jwkSet;

    @GetMapping("/.well-known/jwks.json")
    public Mono<Map<String, Object>> jwks() {
        return Mono.just(jwkSet.toJSONObject());
    }

}
