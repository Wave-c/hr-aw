package com.wave.profile_service.services;

import org.springframework.stereotype.Service;

import com.wave.dtos.UserCreatedEvent;
import com.wave.profile_service.models.dtos.ProfileDto;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserEventKafkaHandler {
    private final ProfileService profileService;

    public Mono<Void> handle(UserCreatedEvent event) {
        return profileService.createProfile(new ProfileDto(
            event.userId(), null, null, null, null));
    }
}
