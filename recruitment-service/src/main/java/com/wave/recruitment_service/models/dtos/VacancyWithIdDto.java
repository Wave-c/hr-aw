package com.wave.recruitment_service.models.dtos;

import java.util.UUID;

public record VacancyWithIdDto(
    UUID id,
    VacancyDto vacancy
) { }
