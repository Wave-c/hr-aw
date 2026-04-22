package com.wave.user_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.wave.user_service.models.AuthUser;

import reactor.core.publisher.Mono;

@Repository
public interface AuthUserRepository extends ReactiveCrudRepository<AuthUser, UUID> {
    Mono<AuthUser> findByUsername(String username);
}
