package com.wave.recommendations_service.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wave.dtos.ApplicationDto;
import com.wave.dtos.VacancyDto;
import com.wave.recommendations_service.components.MlClient;
import com.wave.recommendations_service.components.TextBuilder;
import com.wave.recommendations_service.models.CandidateItem;
import com.wave.recommendations_service.models.RecommendationResult;
import com.wave.recommendations_service.models.ScoreResponseItem;

import reactor.core.publisher.Mono;

@Service
public class RecommendationService {

    private final MlClient mlClient;
    private final TextBuilder textBuilder;

    public RecommendationService(MlClient mlClient, TextBuilder textBuilder) {
        this.mlClient = mlClient;
        this.textBuilder = textBuilder;
    }

    public Mono<List<RecommendationResult>> rank(
            VacancyDto vacancy,
            List<ApplicationDto> applications
    ) {

        String vacancyText = textBuilder.buildVacancyText(vacancy);

        List<CandidateItem> candidates = applications.stream()
            .map(app -> new CandidateItem(
                    app.id(),
                    textBuilder.buildCandidateText(app)
            ))
            .toList();

        return mlClient.score(vacancyText, candidates)
                .map(scores -> applyBusinessRules(applications, scores));
    }

    private List<RecommendationResult> applyBusinessRules(
        List<ApplicationDto> apps,
        List<ScoreResponseItem> scores
    ) {

        Map<UUID, ApplicationDto> appMap = apps.stream()
                .collect(Collectors.toMap(ApplicationDto::id, a -> a));

        List<RecommendationResult> results = new ArrayList<>();

        for (ScoreResponseItem item : scores) {

            ApplicationDto app = appMap.get(item.id());

            double finalScore = item.score()
                    + salaryBonus(app)
                    + coverLetterBonus(app);

            results.add(new RecommendationResult(app.id(), finalScore));
        }

        return results.stream()
                .sorted(Comparator.comparing(RecommendationResult::score).reversed())
                .toList();
    }

    private double salaryBonus(ApplicationDto app) {
        return app.expectedSalary() != null ? 0.05 : 0.0;
    }

    private double coverLetterBonus(ApplicationDto app) {
        return (app.coverLetter() != null && !app.coverLetter().isBlank())
                ? 0.03 : 0.0;
    }
}
