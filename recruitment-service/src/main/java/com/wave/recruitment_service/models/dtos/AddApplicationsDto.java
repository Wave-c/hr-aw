package com.wave.recruitment_service.models.dtos;

import java.util.List;
import java.util.UUID;

import com.wave.dtos.ApplicationDto;

import lombok.NonNull;

public record AddApplicationsDto(
    @NonNull UUID vacancyId,
    List<ApplicationDto> applications
) { }
