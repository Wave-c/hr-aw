package com.wave.recruitment_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.dtos.ApplicationDto;
import com.wave.dtos.VacancyDto;
import com.wave.dtos.VacancyWithApplications;
import com.wave.recruitment_service.models.Vacancy;
import com.wave.recruitment_service.models.dtos.AddAvailableDto;
import com.wave.recruitment_service.models.dtos.VacancyWithIdDto;
import com.wave.recruitment_service.services.ApplicationService;
import com.wave.recruitment_service.services.VacancyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
public class VacancyController {
    private final VacancyService vacancyService;
    private final ApplicationService applicationService;

    @PostMapping("/add")
    public Mono<ResponseEntity<UUID>> addVacancy(@RequestBody VacancyDto vacancyDto, @RequestHeader("X-User-Id") UUID userId) {
        return vacancyService.addVacancy(vacancyDto, userId)
            .map(v -> ResponseEntity.ok(v.getId()))
            .doOnError(err -> log.error("Controller error", err));
    }

    @PatchMapping("/add-available")
    public Mono<ResponseEntity<String>> addUsersToAvailable(@RequestHeader("X-User-Id") UUID userId, @RequestBody AddAvailableDto addAvailableDto) {
        log.info("Received request to add users {} to available for vacancy {}", addAvailableDto.availableFor(), addAvailableDto.vacancyId());
        return vacancyService.addUsersToAvailable(addAvailableDto, userId)
            .doOnError(err -> log.error("Controller error", err))
            .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PatchMapping("/remove-available")
    public Mono<ResponseEntity<String>> removeUsersFromAvailable(@RequestHeader("X-User-Id") UUID userId, @RequestBody AddAvailableDto addAvailableDto) {
        log.info("Received request to remove users {} from available for vacancy {}", addAvailableDto.availableFor(), addAvailableDto.vacancyId());
        return vacancyService.removeUsersFromAvailable(addAvailableDto, userId)
            .doOnError(err -> log.error("Controller error", err))
            .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> deleteVacancy(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return vacancyService.deleteVacancy(id, userId)
            .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping("/my")
    public Flux<VacancyWithIdDto> getMyVacancies(@RequestHeader("X-User-Id") UUID userId) {
        return vacancyService.getMyVacancies(userId)
            .map(v -> new VacancyWithIdDto(v.getId(), new VacancyDto(
                v.getTitle(),
                v.getDescription(),
                v.getFormats(),
                v.getTags(),
                v.getSalaryFrom(),
                v.getSalaryTo()
            )));
    }

    @PatchMapping("/update/{id}")
    public Mono<ResponseEntity<String>> updateVacancy(@PathVariable UUID id, @RequestBody VacancyDto vacancyDto, @RequestHeader("X-User-Id") UUID userId) {
        return vacancyService.updateVacancy(id, vacancyDto, userId)
            .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<VacancyDto>> getVacancy(@PathVariable UUID id) {
        return vacancyService.getVacancy(id)
            .map(v -> ResponseEntity.ok(new VacancyDto(
                v.getTitle(),
                v.getDescription(),
                v.getFormats(),
                v.getTags(),
                v.getSalaryFrom(),
                v.getSalaryTo()
            )));
    }

    @GetMapping("/get-available-for/{id}")
    public Flux<UUID> getAvailableFor(@PathVariable UUID id) {
    return vacancyService.getVacancy(id)
            .map(Vacancy::getAvailableFor)
            .flatMapMany(list -> Flux.fromIterable(
                    Optional.ofNullable(list).orElseGet(List::of)
            ));
    }


    @GetMapping("/with-applications/{id}")
    public Mono<VacancyWithApplications> getVacancyWithApplications(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return Mono.zip(
            vacancyService.getVacancy(id),
            applicationService.getByVacancyId(id, userId).collectList()
        ).map(tuple -> new VacancyWithApplications(
            new VacancyDto(
                tuple.getT1().getTitle(),
                tuple.getT1().getDescription(),
                tuple.getT1().getFormats(),
                tuple.getT1().getTags(),
                tuple.getT1().getSalaryFrom(),
                tuple.getT1().getSalaryTo()
            ),
            tuple.getT2().stream().map(a -> new ApplicationDto(
                a.getId(),
                a.getFirstName(),
                a.getLastName(),
                a.getPatronymic(),
                a.getResumeText(),
                a.getCoverLetter(),
                a.getExpectedSalary(),
                a.getStatus()
            )).toList()
        ));
    }


}
