package com.wave.profile_service.models.dtos;

public record ProfileDto(
    String firstName,
    String lastName,
    String patronymic,
    String phone
) { }
