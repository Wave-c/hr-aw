package com.wave.recommendations_service.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.recommendations_service.models.ApplicationDto;
import com.wave.recommendations_service.models.RecommendationResult;
import com.wave.recommendations_service.models.VacancyDto;
import com.wave.recommendations_service.services.RecommendationService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @PostMapping("/{vacancyId}")
    public Mono<List<RecommendationResult>> rank(
            @PathVariable UUID vacancyId
    ) {
        // здесь пока mock, позже заменим на repo/service
        VacancyDto vacancy = mockVacancy(vacancyId);
        List<ApplicationDto> apps = mockApplications(vacancyId);

        log.info(apps);

        return service.rank(vacancy, apps);
    }

    private VacancyDto mockVacancy(UUID id) {
        return new VacancyDto(
                id,
                "Java Backend Developer",
                """
                We are looking for a Java Backend Developer with experience in Spring Boot,
                microservices architecture, REST APIs, and PostgreSQL. Experience with Kafka
                is a plus. You will work on high-load distributed systems.
                """,
                List.of("REMOTE", "FULL_TIME"),
                List.of("Java", "Spring Boot", "PostgreSQL", "Kafka", "Microservices"),
                3000,
                5000
        );
    }

    private List<ApplicationDto> mockApplications(UUID vacancyId) {

        return List.of(
                // 😐 Джун
                new ApplicationDto(
                        UUID.fromString("103d1d64-605a-4b30-8876-b18eed38b83c"),
                        vacancyId,
                        """
                        Junior Java developer with 1 year experience.
                        Spring Boot, small pet projects, basic SQL.
                        """,
                        "Looking to grow as a backend developer.",
                        2000
                ),

                // 🔥 Сильный кандидат
                new ApplicationDto(
                        UUID.fromString("2c082d5c-fe03-4ec7-a272-53dcf45a2692"),
                        vacancyId,
                        """
                        Java backend developer with 5 years of experience.
                        Worked with Spring Boot, microservices, PostgreSQL, Kafka.
                        Built high-load systems and REST APIs.
                        """,
                        "I have strong experience with your tech stack and high-load systems.",
                        4500
                ),

                // 🤔 Смежный (DevOps)
                new ApplicationDto(
                        UUID.fromString("7b06deba-c41a-42fb-a464-484f8ff337ff"),
                        vacancyId,
                        """
                        DevOps engineer with experience in Docker, Kubernetes, CI/CD.
                        Some experience with Java services and backend systems.
                        """,
                        null,
                        4000
                ),

                // 👍 Средний (без Kafka)
                new ApplicationDto(
                        UUID.fromString("71a35b68-51b6-4e34-ba34-a92e3b279516"),
                        vacancyId,
                        """
                        Backend developer with 4 years experience.
                        Java, Spring Boot, REST APIs, MySQL.
                        Worked on monolithic and microservice systems.
                        """,
                        null,
                        3500
                ),

                // ❌ Нерелевантный (frontend)
                new ApplicationDto(
                        UUID.fromString("54b7998d-f5f7-4d04-b625-626b80702097"),
                        vacancyId,
                        """
                        Frontend developer with React and TypeScript.
                        Built UI applications and dashboards.
                        """,
                        "I want to switch to backend.",
                        3000
                )
        );
    }
}