package com.wave.dtos;

import java.util.UUID;

import com.wave.components.ApplicationStatus;

import lombok.NonNull;

public record ApplicationDto(
    UUID id,
    @NonNull String firstName,
    @NonNull String lastName,
    String patronymic,
    @NonNull String resumeText,
    String coverLetter,
    Integer expectedSalary,
    ApplicationStatus status
) { }
