package com.wave.recommendations_service.components;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.wave.recommendations_service.models.CandidateItem;
import com.wave.recommendations_service.models.ScoreRequest;
import com.wave.recommendations_service.models.ScoreResponseItem;

import reactor.core.publisher.Mono;

@Service
public class MlClient {
    @Value("${services.ml-service}")
    private String mlServiceUrl;

    private final WebClient webClient;

    public MlClient(
            WebClient.Builder builder,
            @Value("${services.ml-service}") String mlServiceUrl
    ) {
        this.webClient = builder
                .baseUrl(mlServiceUrl)
                .build();
    }

    public Mono<List<ScoreResponseItem>> score(
        String vacancy,
        List<CandidateItem> candidates
    ) {

        ScoreRequest request = new ScoreRequest(vacancy, candidates);

        return webClient.post()
                .uri("/score")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(ScoreResponseItem.class)
                .collectList();
    }
}
