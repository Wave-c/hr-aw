package com.wave.dtos;

import java.util.List;

public record VacancyWithApplications(
    VacancyDto vacancy,
    List<ApplicationDto> applications
) { }
