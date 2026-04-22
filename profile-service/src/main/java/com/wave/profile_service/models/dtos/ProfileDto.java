package com.wave.profile_service.models.dtos;

import java.util.UUID;

public record ProfileDto(
    UUID id,
    String firstName,
    String lastName,
    String patronymic,
    String phone
) { }
