package com.wave.recruitment_service.services;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.wave.components.ApplicationStatus;
import com.wave.dtos.ApplicationStatusChangedEvent;
import com.wave.recruitment_service.exceptions.NotFoundException;
import com.wave.recruitment_service.models.Application;
import com.wave.recruitment_service.models.dtos.AddApplicationsDto;
import com.wave.recruitment_service.repositories.ApplicationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final VacancyService vacancyService;
    private final ApplicationEventProducer applicationEventProducer;

    public Mono<Void> addApplications(AddApplicationsDto addApplicationsDto) {
        log.info("Adding applications for vacancy {}", addApplicationsDto.vacancyId());
        return Mono.defer(() -> {
            log.info(">>> AFTER defer");
        return vacancyService.getVacancyById(addApplicationsDto.vacancyId())
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMapMany(v -> Flux.fromIterable(addApplicationsDto.applications())
                .map(a -> new Application(
                    UUID.randomUUID(),
                    a.firstName(),
                    a.lastName(),
                    a.patronymic(),
                    addApplicationsDto.vacancyId(),
                    a.resumeText(),
                    a.coverLetter(),
                    a.expectedSalary(),
                    ApplicationStatus.SUBMITTED,
                    true,
                    ApplicationStatus.findNode(ApplicationStatus.SUBMITTED)
                )))
            .flatMap(applicationRepository::save)
            .then()
            .doOnError(err -> log.error("Error occurred while adding applications", err));});
    }

    public Flux<Application> getByVacancyId(UUID vacancyId, UUID userId) {
        return vacancyService.getVacancyById(vacancyId)
            .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
            .flatMapMany(v -> {
                if (!v.getCreatedBy().equals(userId) &&
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
                    if (!v.getCreatedBy().equals(userId) &&
                        !v.getAvailableFor().contains(userId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    return Mono.just(a);
                })
            );
    }

    public Mono<Void> nextStatus(UUID applicationId, UUID userId) {
        return applicationRepository.findById(applicationId)
            .switchIfEmpty(Mono.error(new NotFoundException("Application not found")))
            .flatMap(a -> vacancyService.getVacancyById(a.getVacancyId())
                .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
                .flatMap(v -> {
                    if (!v.getCreatedBy().equals(userId) &&
                        !v.getAvailableFor().contains(userId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    if (a.getCurrentNode().getNext() == null) {
                        return Mono.error(new IllegalStateException("Application is already in final status"));
                    }
                    a.setCurrentNode(a.getCurrentNode().getNext());
                    a.setStatus(a.getCurrentNode().getData());
                    a.setNew(false);
                    return applicationRepository.save(a)
                        .then(applicationEventProducer.send(
                            ApplicationStatusChangedEvent
                                .builder()
                                .applicationId(a.getId())
                                .newStatus(a.getStatus())
                                .build()
                        ));
                })
            );
    }

    public Mono<Void> reject(UUID applicationId, UUID userId) {
        return applicationRepository.findById(applicationId)
            .switchIfEmpty(Mono.error(new NotFoundException("Application not found")))
            .flatMap(a -> vacancyService.getVacancyById(a.getVacancyId())
                .switchIfEmpty(Mono.error(new NotFoundException("Vacancy not found")))
                .flatMap(v -> {
                    if (!v.getCreatedBy().equals(userId) &&
                        !v.getAvailableFor().contains(userId)) {
                        return Mono.error(new AccessDeniedException("Access denied"));
                    }
                    a.setCurrentNode(null);
                    a.setStatus(ApplicationStatus.REJECTED);
                    a.setNew(false);
                    return applicationRepository.save(a)
                        .then(applicationEventProducer.send(
                            ApplicationStatusChangedEvent
                                .builder()
                                .applicationId(a.getId())
                                .newStatus(a.getStatus())
                                .build()
                        ));
                })
            );
    }
}
