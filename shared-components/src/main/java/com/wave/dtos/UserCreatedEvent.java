package com.wave.dtos;

import java.time.Instant;
import java.util.UUID;

public record UserCreatedEvent(
    UUID eventId,
    UUID userId,
    String username,
    Instant createdAt
) {}
