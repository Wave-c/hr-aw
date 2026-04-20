package com.wave.recruitment_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.recruitment_service.models.dtos.AddApplicationsDto;
import com.wave.recruitment_service.models.dtos.ApplicationDto;
import com.wave.recruitment_service.services.ApplicationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/add-all")
    public Mono<ResponseEntity<String>> addAll(@RequestBody AddApplicationsDto addApplicationsDto) {
        return applicationService.addApplications(addApplicationsDto)
            .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping("/get-by-vacancy/{id}")
    public Flux<ApplicationDto> getApplicationsByVacancyId(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return applicationService.getByVacancyId(id, userId)
            .map(a -> new ApplicationDto(
                a.getId(),
                a.getResumeText(),
                a.getCoverLetter(),
                a.getExpectedSalary()
            ));
    }

    @GetMapping("/{id}")
    public Mono<ApplicationDto> getById(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return applicationService.getById(id, userId)
            .map(a -> new ApplicationDto(
                a.getId(),
                a.getResumeText(),
                a.getCoverLetter(),
                a.getExpectedSalary()
            ));
    }
}
