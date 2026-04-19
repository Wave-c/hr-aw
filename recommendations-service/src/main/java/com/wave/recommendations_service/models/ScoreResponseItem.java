package com.wave.recommendations_service.models;

import java.util.UUID;

public record ScoreResponseItem(
    UUID id,
    double score
) {}
