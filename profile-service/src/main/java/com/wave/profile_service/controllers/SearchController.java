package com.wave.profile_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.profile_service.models.dtos.ProfileSearchDto;
import com.wave.profile_service.services.ProfileService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final ProfileService profileService;

    @GetMapping("/by-name")
    public Flux<ProfileSearchDto> search(@RequestParam String query) {
        return profileService.search(query)
            .map(p -> new ProfileSearchDto(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getPatronymic()
            ));
    }
}
