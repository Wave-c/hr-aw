package com.wave.user_service.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RSAKey rsaKey;
    // private final RefreshTokenStorageService refreshTokenStorage;

    private static final Duration ACCESS_EXP = Duration.ofDays(1);
    private static final Duration REFRESH_EXP = Duration.ofDays(7);

    public Mono<String> generateAccessToken(UUID userId, List<String> roles) {
        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .subject(userId.toString())
            .claim("roles", roles)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plus(ACCESS_EXP)))
            .build();

        return sign(claims);
    }

    public Mono<String> generateRefreshToken(UUID userId) {
        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .subject(userId.toString())
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plus(REFRESH_EXP)))
            .claim("type", "refresh")
            .build();

        return sign(claims)
            // .flatMap(token -> refreshTokenStorage.store(token, userId))
        ;
    }

    private Mono<String> sign(JWTClaimsSet claims) {
        try {
            JWSSigner signer = new RSASSASigner(rsaKey.toPrivateKey());

            SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .build(), claims);

            jwt.sign(signer);
            return Mono.just(jwt.serialize());
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to sign JWT", e));
        }
    }
}
