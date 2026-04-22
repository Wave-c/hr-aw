package com.wave.profile_service.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.profile_service.models.dtos.ProfileDto;
import com.wave.profile_service.services.ProfileService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService profileService;

    @PatchMapping("/me")
    public Mono<ResponseEntity<String>> updateProfile(@RequestHeader("X-User-Id") UUID userId, @RequestBody ProfileDto profileDto) {
        return profileService.updateProfile(userId, profileDto)
            .thenReturn(ResponseEntity.ok("Profile updated successfully"))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<ProfileDto>> getProfile(@RequestHeader("X-User-Id") UUID userId) {
        System.out.println("Received request to get profile for user " + userId);
        return profileService.getProfile(userId)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

}
