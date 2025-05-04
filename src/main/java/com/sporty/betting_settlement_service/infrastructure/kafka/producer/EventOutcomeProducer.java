package com.sporty.betting_settlement_service.infrastructure.kafka.producer;

import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeProducer {

    private final KafkaTemplate<String, EventOutcomeDto> kafkaTemplate;

    @Value("${spring.kafka.template.default-topic:event-outcomes}")
    private String topic;

    /**
     * Non-transactional, async send with callback handled by ProducerListener
     */
    public void sendAsync(EventOutcomeDto dto) {
        kafkaTemplate.send(topic, dto.eventId().toString(), dto);
    }

}
