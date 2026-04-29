package com.wave.recruitment_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.dtos.ApplicationDto;
import com.wave.recruitment_service.models.dtos.AddApplicationsDto;
import com.wave.recruitment_service.models.dtos.ApplicationActionDto;
import com.wave.recruitment_service.services.ApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/add-all")
    public Mono<ResponseEntity<Object>> addAll(@RequestBody AddApplicationsDto dto) {
        return applicationService.addApplications(dto)
                .thenReturn(ResponseEntity.ok().build())
                .doOnError(err -> log.error("Controller error", err));
    }

    @GetMapping("/get-by-vacancy/{id}")
    public Flux<ApplicationDto> getApplicationsByVacancyId(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return applicationService.getByVacancyId(id, userId)
            .map(a -> new ApplicationDto(
                a.getId(),
                a.getFirstName(),
                a.getLastName(),
                a.getPatronymic(),
                a.getResumeText(),
                a.getCoverLetter(),
                a.getExpectedSalary(),
                a.getStatus()
            ));
    }

    @GetMapping("/{id}")
    public Mono<ApplicationDto> getById(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return applicationService.getById(id, userId)
            .map(a -> new ApplicationDto(
                a.getId(),
                a.getFirstName(),
                a.getLastName(),
                a.getPatronymic(),
                a.getResumeText(),
                a.getCoverLetter(),
                a.getExpectedSalary(),
                a.getStatus()
            ));
    }

    @PostMapping("/reject")
    public Mono<ResponseEntity<String>> reject(@RequestBody ApplicationActionDto applicationId, @RequestHeader("X-User-Id") UUID userId) {
        log.info("Received request to reject application {} from user {}", applicationId, userId);
        return applicationService.reject(applicationId.applicationId(), userId)
            .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/apply") //TODO:: обработка попытки "продвинуть" заявку, которая уже отклонена
    public Mono<ResponseEntity<String>> applyNext(@RequestBody ApplicationActionDto applicationId, @RequestHeader("X-User-Id") UUID userId) {
        return applicationService.nextStatus(applicationId.applicationId(), userId)
            .thenReturn(ResponseEntity.ok().build());
    }

}
