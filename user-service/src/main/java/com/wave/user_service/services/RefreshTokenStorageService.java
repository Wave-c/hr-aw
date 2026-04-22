package com.wave.user_service.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class RefreshTokenStorageService {
    private final ReactiveValueOperations<String, String> valueOps;
    private final MessageDigest digest;

    public RefreshTokenStorageService(@Qualifier("refreshTokenStorageOperations")
        ReactiveRedisOperations<String, String> ops, MessageDigest digest) {
        this.valueOps = ops.opsForValue();
        this.digest = digest;
    }

    private String getKey(String token) {
        byte[] encodedHash = digest.digest(token.getBytes(
            StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return "refresh:" + hexString.toString();
    }

    public Mono<String> store(String token, UUID userId) {
        return valueOps.set(getKey(token), userId.toString(),
            Duration.ofDays(15))
            .thenReturn(token);
    }

    public Mono<UUID> getUserId(String token) {
        return valueOps.get(getKey(token)).map(UUID::fromString);
    }

    public Mono<Boolean> delete(String token) {
        if (token == null || token.isEmpty()) {
            return Mono.just(false);
        }

        return valueOps.delete(getKey(token));
    }
}
