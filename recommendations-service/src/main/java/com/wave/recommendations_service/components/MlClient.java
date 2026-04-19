package com.wave.recommendations_service.components;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.wave.recommendations_service.models.CandidateItem;
import com.wave.recommendations_service.models.ScoreRequest;
import com.wave.recommendations_service.models.ScoreResponseItem;

import reactor.core.publisher.Mono;

@Service
public class MlClient {

    private final WebClient webClient;

    public MlClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://192.168.77.10:8000").build();
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
