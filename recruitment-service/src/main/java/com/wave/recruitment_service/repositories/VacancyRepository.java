package com.wave.recruitment_service.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.wave.recruitment_service.models.Vacancy;

@Repository
public interface VacancyRepository extends ReactiveCrudRepository<Vacancy, UUID> {

}
