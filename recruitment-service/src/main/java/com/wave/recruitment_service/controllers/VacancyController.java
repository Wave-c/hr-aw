package com.wave.recruitment_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.recruitment_service.models.dtos.AddAvailableDto;
import com.wave.recruitment_service.models.dtos.VacancyDto;
import com.wave.recruitment_service.models.dtos.VacancyWithIdDto;
import com.wave.recruitment_service.services.VacancyService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/vacancies")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/add")
    public Mono<ResponseEntity<UUID>> addVacancy(@RequestBody VacancyDto vacancyDto, @RequestHeader("X-User-Id") UUID userId) {
        return vacancyService.addVacancy(vacancyDto, userId)
            .map(v -> ResponseEntity.ok(v.getId()));
    }

    @PatchMapping("/add-available")
    public Mono<ResponseEntity<String>> addUsersToAvailable(@RequestHeader("X-User-Id") UUID userId, @RequestBody AddAvailableDto addAvailableDto) {
        return vacancyService.addUsersToAvailable(addAvailableDto, userId)
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

}
