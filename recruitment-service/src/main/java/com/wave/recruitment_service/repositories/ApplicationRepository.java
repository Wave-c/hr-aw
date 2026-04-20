package com.wave.recruitment_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.wave.recruitment_service.models.Application;

import reactor.core.publisher.Flux;

@Repository
public interface ApplicationRepository extends ReactiveCrudRepository<Application, UUID> {
    Flux<Application> findByVacancyId(UUID vacancyId);
}
