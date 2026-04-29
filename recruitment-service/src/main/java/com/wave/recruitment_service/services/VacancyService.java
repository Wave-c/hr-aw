package com.wave.recruitment_service.services;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wave.dtos.VacancyDto;
import com.wave.recruitment_service.exceptions.NotFoundException;
import com.wave.recruitment_service.models.Vacancy;
import com.wave.recruitment_service.models.dtos.AddAvailableDto;
import com.wave.recruitment_service.repositories.VacancyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;

    public Mono<Vacancy> getVacancyById(UUID id) {
        return vacancyRepository.findById(id)
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("Vacancy not found for id {}", id);
                return Mono.error(new NotFoundException("Vacancy not found"));
            }))
            .doOnNext(v -> log.info(v))
            .doOnError(err -> log.error("Error occurred while adding applications", err));
    }

    public Mono<Vacancy> addVacancy(VacancyDto vacancyDto, UUID createdBy) {
        return vacancyRepository.save(new Vacancy(
            UUID.randomUUID(),
            vacancyDto.title(),
            vacancyDto.description(),
            vacancyDto.formats(),
            vacancyDto.tags(),
            vacancyDto.salaryFrom(),
            vacancyDto.salaryTo(),
            createdBy,
            List.of(createdBy),
            true
        ))
        .doOnError(err -> log.error("Error occurred while adding vacancy", err));
    }

    public Mono<Void> addUsersToAvailable(AddAvailableDto addAvailableDto, UUID userId) {
        log.info("Adding users {} to available for vacancy {}", addAvailableDto.availableFor(), addAvailableDto.vacancyId());
        return vacancyRepository.findById(addAvailableDto.vacancyId())
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMap(v -> {
                if (!v.getCreatedBy().equals(userId) &&
                    !v.getAvailableFor().contains(userId)) {
                    return Mono.error(new AccessDeniedException("Only creator can add users to available"));
                }
                var availableFor = v.getAvailableFor();
                availableFor.addAll(addAvailableDto.availableFor());
                v.setAvailableFor(availableFor
                    .stream().distinct().collect(Collectors.toList())
                );
                v.setNew(false);
                return vacancyRepository.save(v);
            })
            .then();
    }

    public Mono<Void> removeUsersFromAvailable(AddAvailableDto addAvailableDto, UUID userId) {
        log.info("Removing users {} from available for vacancy {}", addAvailableDto.availableFor(), addAvailableDto.vacancyId());
        return vacancyRepository.findById(addAvailableDto.vacancyId())
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMap(v -> {

                if (!v.getCreatedBy().equals(userId) &&
                    !v.getAvailableFor().contains(userId)) {
                    return Mono.error(new AccessDeniedException("Only creator can remove users from available"));
                }

                List<UUID> toRemove = Optional.ofNullable(addAvailableDto.availableFor())
                        .orElseGet(List::of);

                List<UUID> updated = Optional.ofNullable(v.getAvailableFor())
                        .orElseGet(List::of)
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(id -> !toRemove.contains(id))
                        .toList();

                v.setAvailableFor(updated);
                v.setNew(false);

                return vacancyRepository.save(v);
            })
            .then();
    }

    public Mono<Void> deleteVacancy(UUID id, UUID userId) {
        return vacancyRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMap(v -> {
                if (!v.getCreatedBy().equals(userId)) {
                    return Mono.error(new AccessDeniedException("Only creator can delete vacancy"));
                }
                return vacancyRepository.deleteById(id);
            });
    }

    public Flux<Vacancy> getMyVacancies(UUID userId) {
        return vacancyRepository.findAll()
            .filter(v -> v.getCreatedBy().equals(userId)
                || v.getAvailableFor().contains(userId));
    }

    public Mono<Vacancy> getVacancy(UUID id) {
        return vacancyRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")));
    }

    public Mono<Void> updateVacancy(UUID id, VacancyDto vacancyDto, UUID userId) {
        return vacancyRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMap(v -> {
                if (!v.getCreatedBy().equals(userId)) {
                    return Mono.error(new AccessDeniedException("Only creator can update vacancy"));
                }
                setIfNotNull(vacancyDto.title(), v::setTitle);
                setIfNotNull(vacancyDto.description(), v::setDescription);
                setIfNotNull(vacancyDto.formats(), v::setFormats);
                setIfNotNull(vacancyDto.tags(), v::setTags);
                setIfNotNull(vacancyDto.salaryFrom(), v::setSalaryFrom);
                setIfNotNull(vacancyDto.salaryTo(), v::setSalaryTo);
                v.setNew(false);
                return vacancyRepository.save(v);
            })
            .then();
    }

    private <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
