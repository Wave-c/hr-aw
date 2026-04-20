package com.wave.recruitment_service.models.dtos;

import java.util.UUID;

import lombok.NonNull;

public record ApplicationDto(
    UUID id,
    @NonNull String resumeText,
    String coverLetter,
    Integer expectedSalary
) { }
