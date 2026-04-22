package com.wave.user_service.models.dtos;

public record LoginRequest(
    String username,
    String password
) {
}