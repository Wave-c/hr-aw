package com.wave.user_service.services;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import com.wave.dtos.UserCreatedEvent;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaSender<String, String> sender;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "user.events";

    public Mono<Void> sendUserCreated(UserCreatedEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(json -> {

                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(TOPIC, event.userId().toString(), json);

                    SenderRecord<String, String, String> senderRecord =
                            SenderRecord.create(record, event.eventId().toString());

                    return sender.send(Mono.just(senderRecord))
                            .next()
                            .doOnNext(result -> {
                                RecordMetadata meta = result.recordMetadata();
                                System.out.println("Sent to partition=" + meta.partition()
                                        + " offset=" + meta.offset());
                            })
                            .then();
                });
    }
}
