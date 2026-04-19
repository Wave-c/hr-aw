package com.wave.recommendations_service.models;

import java.util.UUID;

public record RecommendationResult(
    UUID applicationId,
    double score
) {}
