package com.wave.profile_service.configurations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.kafka.common.serialization.StringDeserializer;
import com.wave.dtos.UserCreatedEvent;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ReceiverOptions<String, UserCreatedEvent> receiverOptions() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "profile-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return ReceiverOptions.<String, UserCreatedEvent>create(props)
            .withValueDeserializer(new JsonDeserializer<>(UserCreatedEvent.class));
    }

    @Bean
    public KafkaReceiver<String, UserCreatedEvent> kafkaReceiver(
            ReceiverOptions<String, UserCreatedEvent> receiverOptions) {
        return KafkaReceiver.create(receiverOptions.subscription(Collections.singleton("user.events")));
    }

    @Bean
    public Flux<ReceiverRecord<String, UserCreatedEvent>> kafkaFlux(
            KafkaReceiver<String, UserCreatedEvent> receiver) {

        return receiver.receive();
    }
}
