package com.wave.profile_service.models.dtos;

import java.util.UUID;

public record ProfileSearchDto(
    UUID id,
    String firstName,
    String lastName,
    String patronymic
) { }
