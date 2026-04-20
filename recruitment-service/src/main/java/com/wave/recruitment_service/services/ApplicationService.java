package com.wave.recruitment_service.services;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wave.recruitment_service.exceptions.NotFoundException;
import com.wave.recruitment_service.models.Application;
import com.wave.recruitment_service.models.dtos.AddApplicationsDto;
import com.wave.recruitment_service.repositories.ApplicationRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final VacancyService vacancyService;

    public Mono<Void> addApplications(AddApplicationsDto addApplicationsDto) {
        return vacancyService.getVacancyById(addApplicationsDto.vacancyId())
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMapMany(v -> Flux.fromIterable(addApplicationsDto.applications())
                .map(a -> new Application(
                    UUID.randomUUID(),
                    addApplicationsDto.vacancyId(),
                    a.resumeText(),
                    a.coverLetter(),
                    a.expectedSalary()
                )))
            .flatMap(applicationRepository::save)
             .then();
    }

    public Flux<Application> getByVacancyId(UUID vacancyId, UUID userId) {
        return vacancyService.getVacancyById(vacancyId)
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMapMany(v -> {
                if (!v.getCreatedBy().equals(userId) ||
                    !v.getAvailableFor().contains(userId)) {
                    return Mono.error(new AccessDeniedException("Access denied"));
                }
                return applicationRepository.findByVacancyId(vacancyId);
            });
    }

    public Mono<Application> getById(UUID id, UUID userId) {
        return applicationRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Application not found")))
            .flatMap(a -> vacancyService.getVacancyById(a.getVacancyId())
                .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
                .flatMap(v -> {
                    if (!v.getCreatedBy().equals(userId) ||
                        !v.getAvailableFor().contains(userId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    return Mono.just(a);
                })
            );
    }
}
