package com.wave.user_service.models.dtos;

public record RegistrationRequest(
    String username,
    String password
)
{ }
