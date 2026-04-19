package com.wave.recommendations_service.models;

import java.util.UUID;

public record ApplicationDto(
    UUID id,
    UUID vacancyId,
    String resumeText,
    String coverLetter,
    Integer expectedSalary
) {}
