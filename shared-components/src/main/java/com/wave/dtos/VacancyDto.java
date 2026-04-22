package com.wave.dtos;

import java.util.List;

public record VacancyDto(
    String title,
    String description,
    List<String> formats,
    List<String> tags,
    Integer salaryFrom,
    Integer salaryTo
) { }
