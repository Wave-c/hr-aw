package com.wave.user_service.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wave.dtos.UserCreatedEvent;
import com.wave.user_service.models.AuthUser;
import com.wave.user_service.models.AuthUser.UserRole;
import com.wave.user_service.models.AuthUser.UserStatus;
import com.wave.user_service.models.dtos.RegistrationRequest;
import com.wave.user_service.repositories.AuthUserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import com.wave.user_service.utils.exceptions.UserAlreadyExistException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer eventProducer;

    public Mono<AuthUser> findByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    @Transactional
    public Mono<AuthUser> registerUser(RegistrationRequest regRequest) {
        UUID userId = UUID.randomUUID();

        AuthUser newUser = AuthUser.builder()
            .id(userId)
            .username(regRequest.username())
            .passwordHash(passwordEncoder.encode(regRequest.password()))
            .status(UserStatus.ACTIVE)
            .createdAt(Instant.now())
            .roles(List.of(UserRole.HR))
            .isNew(true)
            .build();

        return findByUsername(regRequest.username())
            .flatMap(existingUser ->
                Mono.<AuthUser>error(new UserAlreadyExistException("Username already exist"))
            )
            .switchIfEmpty(
                authUserRepository.save(newUser)
                    .flatMap(user ->
                        eventProducer.sendUserCreated(new UserCreatedEvent(UUID.randomUUID(), user.getId(), user.getUsername(), Instant.now()))
                            .thenReturn(user))
            );
    }

    public Boolean validateUser(AuthUser user, String password) {
        return passwordEncoder.matches(password, user.getPassword()) &&
            user.getStatus() != UserStatus.SUSPENDED;
    }

    public Mono<List<String>> getRoles(UUID userId) {
        return authUserRepository.findById(userId)
            .map(AuthUser::getRoles)
            .map(roles -> roles.stream()
                .map(Enum::name)
                .toList()
            );
    }
}