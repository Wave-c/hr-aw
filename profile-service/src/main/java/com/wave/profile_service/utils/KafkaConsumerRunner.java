package com.wave.profile_service.utils;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import com.wave.dtos.UserCreatedEvent;
import com.wave.profile_service.services.UserEventKafkaHandler;

import lombok.RequiredArgsConstructor;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@Component
@RequiredArgsConstructor
public class KafkaConsumerRunner implements SmartLifecycle {

    private final Flux<ReceiverRecord<String, UserCreatedEvent>> kafkaFlux;
    private final UserEventKafkaHandler handler;

    private Disposable subscription;

    @Override
    public void start() {

        subscription = kafkaFlux
            .flatMap(record ->
                handler.handle(record.value())
                    .then(Mono.fromRunnable(record.receiverOffset()::acknowledge))
            )
            .onErrorContinue((err, obj) ->
                System.err.println("Kafka error: " + err)
            )
            .subscribe();
    }

    @Override
    public void stop() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    @Override
    public boolean isRunning() {
        return subscription != null && !subscription.isDisposed();
    }
}