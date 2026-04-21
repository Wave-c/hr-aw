package com.wave.recruitment_service.utils;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;

import com.wave.components.ApplicationStatus;
import com.wave.recruitment_service.models.Application;

import reactor.core.publisher.Mono;

@Component
public class ApplicationAfterConvertCallback implements AfterConvertCallback<Application> {
    @Override
    public Publisher<Application> onAfterConvert(Application entity, SqlIdentifier table) {
        entity.setCurrentNode(ApplicationStatus.findNode(entity.getStatus()));
        return Mono.just(entity);
    }
}