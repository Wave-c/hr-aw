package com.wave.recruitment_service.services;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wave.recruitment_service.exceptions.NotFoundException;
import com.wave.recruitment_service.models.Vacancy;
import com.wave.recruitment_service.models.dtos.AddAvailableDto;
import com.wave.recruitment_service.models.dtos.VacancyDto;
import com.wave.recruitment_service.repositories.VacancyRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;

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
            List.of(createdBy)
        ));
    }

    public Mono<Void> addUsersToAvailable(AddAvailableDto addAvailableDto, UUID userId) {
        return vacancyRepository.findById(addAvailableDto.vacancyId())
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMap(v -> {
                if (!v.getCreatedBy().equals(userId)) {
                    return Mono.error(new AccessDeniedException("Only creator can add users to available"));
                }
                var availableFor = v.getAvailableFor();
                availableFor.addAll(addAvailableDto.availableFor());
                v.setAvailableFor(availableFor
                    .stream().distinct().collect(Collectors.toList())
                );
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
}
