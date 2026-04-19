package com.wave.recommendations_service.models;

import java.util.UUID;

public record CandidateItem(
    UUID id,
    String text
) {}