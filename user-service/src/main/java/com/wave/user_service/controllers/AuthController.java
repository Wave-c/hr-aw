package com.wave.user_service.controllers;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.wave.user_service.models.dtos.LoginRequest;
import com.wave.user_service.models.dtos.RegistrationRequest;
import com.wave.user_service.services.JwtService;
import com.wave.user_service.services.RefreshTokenStorageService;
import com.wave.user_service.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final RefreshTokenStorageService refreshTokenStorageService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(
        @RequestBody LoginRequest request,
        ServerWebExchange exchange) {
        return userService.findByUsername(request.username())
            .flatMap(user ->
                userService.validateUser(user, request.password()) ?
                    Mono.zip(
                        jwtService.generateAccessToken(
                            user.getId(),
                            List.of("USER")),
                        jwtService.generateRefreshToken(user.getId()))
                        .map(tupple -> {
                            ResponseCookie accessCookie =
                                buildCookie("access_token", tupple.getT1(),
                                    Duration.ofDays(1));
                            ResponseCookie refreshCookie =
                                buildCookie("refresh_token", tupple.getT2(),
                                    Duration.ofDays(7));
                            exchange.getResponse().addCookie(accessCookie);
                            exchange.getResponse().addCookie(refreshCookie);

                            return ResponseEntity.ok(
                                Map.of("userId", user.getId().toString())
                            );
                        }) :
                    Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(
                            Map.of("message",
                            "Invalid login or password")))
            ).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(
                            Map.of("message",
                            "Invalid login or password"))));
    }

    @PostMapping("/registration")
    public Mono<ResponseEntity<Map<String, String>>> registration(
        @RequestBody RegistrationRequest request,
        ServerWebExchange exchange) {
        return userService.registerUser(request)
            .then(login(new LoginRequest(request.username(), request.password()),
                exchange))
            .onErrorResume(err -> {
                log.error(err);
                return Mono.error(err);
            })
            .onErrorReturn(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                    Map.of("message",
                    "Username already exists")));
    }


    @PostMapping("/refresh")
    public Mono<ResponseEntity<Void>> refresh(ServerWebExchange exchange) {
        HttpCookie refreshCookie =
            exchange.getRequest().getCookies().getFirst("refresh_token");

        if (refreshCookie == null) {
            log.info("refresh");
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build());
        }

        return refreshTokenStorageService.getUserId(refreshCookie.getValue())
            .flatMap(userId -> userService.getRoles(userId)
                .flatMap(roles ->
                 jwtService.generateAccessToken(
                    userId, roles)
                    .flatMap(newAccess -> {
                        ResponseCookie accessCookie =
                            buildCookie("access_token", newAccess,
                                Duration.ofMinutes(15));
                        exchange.getResponse().addCookie(accessCookie);

                        return Mono.just(ResponseEntity.ok().build());
                    }))

            );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        HttpCookie refreshCookie = exchange.getRequest().getCookies().getFirst("refresh_token");
        String refreshToken = (refreshCookie != null) ? refreshCookie.getValue() : null;

        return refreshTokenStorageService.delete(refreshToken)
            .flatMap(deleted -> {
                ResponseCookie clearJwt = buildCookie("access_token",
                    "", Duration.ZERO
                );
                ResponseCookie clearRefresh = buildCookie("refresh_token",
                    "", Duration.ZERO
                );

                exchange.getResponse().addCookie(clearJwt);
                exchange.getResponse().addCookie(clearRefresh);

                return Mono.just(ResponseEntity.noContent().build());
            });
    }

    private ResponseCookie buildCookie(String name, String value, Duration maxAge) {
        ResponseCookieBuilder builder = ResponseCookie.from(name)
            .path("/")
            .value(value)
            // .sameSite("None")
            // .secure(true)
            // .httpOnly(true)
            .maxAge(maxAge);

        return builder.build();
    }
}