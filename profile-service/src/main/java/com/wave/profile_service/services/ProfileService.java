package com.wave.profile_service.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wave.profile_service.models.Profile;
import com.wave.profile_service.models.ProfileSearchModel;
import com.wave.profile_service.models.dtos.ProfileDto;
import com.wave.profile_service.repositories.ProfileRepository;
import com.wave.profile_service.repositories.ProfileSearchRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileSearchRepository profileSearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    public Mono<Void> createProfile(ProfileDto profileDto) {
        Profile profile = new Profile(
            profileDto.id(),
            profileDto.firstName(),
            profileDto.lastName(),
            profileDto.patronymic(),
            profileDto.phone(),
            false,
            true
        );
        log.info("Creating profile for user {}", profile.getId());
        return profileRepository.save(profile)
            .then();
    }

    public Mono<Void> updateProfile(UUID userId, ProfileDto profileDto) {
        return profileRepository.findById(userId)
            .flatMap(existingProfile -> {
                setIfNotNull(profileDto.firstName(), existingProfile::setFirstName);
                setIfNotNull(profileDto.lastName(), existingProfile::setLastName);
                setIfNotNull(profileDto.patronymic(), existingProfile::setPatronymic);
                setIfNotNull(profileDto.phone(), existingProfile::setPhone);

                if(existingProfile.getFirstName() != null && existingProfile.getLastName() != null) {
                    existingProfile.setIsActive(true);
                } else {
                    existingProfile.setIsActive(false);
                }

                existingProfile.setNew(false);

                return profileRepository.save(existingProfile)
                    .flatMap(updatedProfile -> {
                        Mono<Void> mbSearchAdd = Mono.empty();
                        if(updatedProfile.getIsActive()) {
                            mbSearchAdd = profileSearchRepository.save(new ProfileSearchModel(
                                updatedProfile.getId(),
                                updatedProfile.getFirstName(),
                                updatedProfile.getLastName(),
                                updatedProfile.getPatronymic()
                            )).then();
                        }
                        return mbSearchAdd;
                    });
            }).then();
    }

    public Mono<ProfileDto> getProfile(UUID userId) {
        return profileRepository.findById(userId)
            .map(p -> new ProfileDto(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getPatronymic(),
                p.getPhone()
            ));
    }

    public Flux<Profile> search(String query) {
        return Mono.fromCallable(() ->
            elasticsearchClient.search(s -> s
                .index("profiles")
                .query(q -> q
                    .bool(b -> b
                        .must(m -> m
                            .multiMatch(mm -> mm
                                .query(query)
                                .fields(
                                    "firstName^2",
                                    "lastName^3",
                                    "patronymic"
                                )
                                .fuzziness("AUTO")
                            )
                        )
                    )).size(10), ProfileSearchModel.class)
            ).flatMapMany(response ->
                Flux.fromIterable(response.hits().hits())
                    .map(hit -> hit.source().getId())
            ).collectList()
            .flatMapMany(ids ->
            profileRepository.findAllById(ids)
                .collectList()
                .map(list -> reorder(list, ids))
                .flatMapMany(Flux::fromIterable)
        );
    }

    private List<Profile> reorder(List<Profile> dbResults, List<UUID> order) {
        Map<UUID, Profile> map = dbResults.stream()
            .collect(Collectors.toMap(p -> p.getId(), p -> p));

        return order.stream()
            .map(map::get)
            .filter(Objects::nonNull)
            .toList();
    }

    private <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
