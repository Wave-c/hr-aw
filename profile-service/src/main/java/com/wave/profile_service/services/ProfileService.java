package com.wave.profile_service.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wave.profile_service.models.Profile;
import com.wave.profile_service.models.ProfileSearchModel;
import com.wave.profile_service.models.dtos.ProfileDto;
import com.wave.profile_service.repositories.ProfileRepository;
import com.wave.profile_service.repositories.ProfileSearchRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileSearchRepository profileSearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    public Mono<Void> createProfile(ProfileDto profileDto) {
        Profile profile = new Profile(
            UUID.randomUUID(),
            profileDto.firstName(),
            profileDto.lastName(),
            profileDto.patronymic(),
            profileDto.phone()
        );
        return profileRepository.save(profile)
            .flatMap(p -> profileSearchRepository.save(new ProfileSearchModel(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getPatronymic()
            )))
            .then();
    }

    public Flux<ProfileSearchModel> search(String query) {
        return Mono.fromCallable(() ->
            elasticsearchClient.search(s -> s
                .index("profiles")
                .query(q -> q
                    .bool(b -> b
                        .must(m -> m
                            .multiMatch(mm -> mm
                                .query(query)
                                .fields(
                                    "firstName",
                                    "lastName",
                                    "patronymic"
                                )
                                .fuzziness("AUTO")
                            )
                        )
                    )).size(10), ProfileSearchModel.class)
            ).flatMapMany(response ->
                Flux.fromIterable(response.hits().hits())
                    .map(hit -> hit.source())
            );
    }
}
