package com.wave.profile_service.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.wave.profile_service.models.dtos.ProfileDto;
import com.wave.profile_service.services.ProfileService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class TestController {
    private final ProfileService profileService;

    @PostMapping("/test-add")
    public Mono<ResponseEntity<String>> add(@RequestBody ProfileDto entity) {
        return profileService.createProfile(entity)
            .thenReturn(ResponseEntity.ok("Profile created successfully"));
    }

}
