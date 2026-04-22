package com.wave.recommendations_service.components;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.wave.dtos.VacancyWithApplications;

import reactor.core.publisher.Mono;

@Service
public class RecruitmentClient {
    private final WebClient webClient;

    public RecruitmentClient(
            WebClient.Builder builder,
            @Value("${services.recruitment-service}") String recruitmentServiceUrl
    ) {
        this.webClient = builder
                .baseUrl(recruitmentServiceUrl)
                .build();
    }

    public Mono<VacancyWithApplications> getApplicationsByVacancyId(UUID vacancyId, UUID userId) {
        return webClient.get()
                .uri("/vacancies/with-applications/" + vacancyId.toString())
                .header("X-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(VacancyWithApplications.class);
    }
}
