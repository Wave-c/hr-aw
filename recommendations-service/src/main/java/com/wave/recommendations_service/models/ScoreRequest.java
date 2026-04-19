package com.wave.recommendations_service.models;

import java.util.List;

public record ScoreRequest(
    String vacancy,
    List<CandidateItem> candidates
) {}