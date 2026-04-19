package com.wave.recommendations_service.models;

import java.util.List;
import java.util.UUID;

public record VacancyDto(
    UUID id,
    String title,
    String description,
    List<String> formats,
    List<String> tags,
    Integer salaryFrom,
    Integer salaryTo
) {}
