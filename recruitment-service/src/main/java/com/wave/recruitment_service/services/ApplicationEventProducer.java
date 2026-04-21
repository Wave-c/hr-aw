package com.wave.recruitment_service.services;

import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import com.wave.dtos.ApplicationStatusChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Log4j2
@Service
@RequiredArgsConstructor
public class ApplicationEventProducer {
    private final KafkaSender<String, ApplicationStatusChangedEvent> kafkaSender;

    private static final String TOPIC = "application-status-events";

    public Mono<Void> send(ApplicationStatusChangedEvent event) {
        SenderRecord<String, ApplicationStatusChangedEvent, UUID> record =
                SenderRecord.create(
                        new ProducerRecord<>(
                                TOPIC,
                                event.getApplicationId().toString(),
                                event
                        ),
                        event.getApplicationId()
                );

        return kafkaSender.send(Mono.just(record))
                .next() // берём результат отправки
                .doOnError(e -> log.error("Kafka send failed", e))
                .then();
    }
}
