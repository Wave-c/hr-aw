package com.wave.user_service.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import lombok.Data;

@Configuration
public class RefreshTokenStorageConfig {
    @Primary
    @Bean("refreshTokenStorageProperties")
    @ConfigurationProperties(prefix = "spring.redis.refresh-token-storage")
    public RedisProperties refreshTokenStorageProperties() {
        return new RedisProperties();
    }

    @Primary
    @Bean("refreshTokenStorageFactory")
    public ReactiveRedisConnectionFactory refreshTokenStorageConnectionFactory(
        @Qualifier("refreshTokenStorageProperties") RedisProperties props) {
        return new LettuceConnectionFactory(props.getHost(), props.getPort());
    }

    @Primary
    @Bean("refreshTokenStorageOperations")
    public ReactiveRedisOperations<String, String> refreshTokenStorageOperations(
        @Qualifier("refreshTokenStorageFactory") ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }

    @Data
    public static class RedisProperties {
        private String host;
        private int port;
    }
}
