package com.wave.recruitment_service.models.dtos;

import java.util.UUID;

import com.wave.dtos.VacancyDto;

public record VacancyWithIdDto(
    UUID id,
    VacancyDto vacancy
) { }
