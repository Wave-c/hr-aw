package com.wave.recruitment_service.models.dtos;

import java.util.List;
import java.util.UUID;

public record AddAvailableDto(
    UUID vacancyId,
    List<UUID> availableFor
) { }
