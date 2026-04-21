package com.wave.recruitment_service.configurations;

import java.util.Map;

import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wave.dtos.ApplicationStatusChangedEvent;

import lombok.RequiredArgsConstructor;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Configuration
@RequiredArgsConstructor
public class EventProducerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public SenderOptions<String, ApplicationStatusChangedEvent> senderOptions() {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        return SenderOptions.create(props);
    }

    @Bean
    public KafkaSender<String, ApplicationStatusChangedEvent> kafkaSender(SenderOptions<String, ApplicationStatusChangedEvent> senderOptions) {
        return KafkaSender.create(senderOptions);
    }
}
