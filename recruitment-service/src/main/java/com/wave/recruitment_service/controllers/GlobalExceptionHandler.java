package com.wave.recruitment_service.controllers;

import java.nio.file.AccessDeniedException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wave.recruitment_service.exceptions.NotFoundException;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<String>> handleAccessDenied(AccessDeniedException ex) {
        return Mono.just(
                ResponseEntity.status(403).body(ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleBadRequest(IllegalArgumentException ex) {
        return Mono.just(
                ResponseEntity.badRequest().body(ex.getMessage())
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<String>> handleNotFound(NotFoundException ex) {
        return Mono.just(
                ResponseEntity.status(404).body(ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        return Mono.just(
                ResponseEntity.status(500).body("Internal server error")
        );
    }
}
