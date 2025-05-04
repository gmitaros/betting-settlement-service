package com.sporty.betting_settlement_service.infrastructure.kafka.consumer;

import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import com.sporty.betting_settlement_service.application.service.EventOutcomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventOutcomeListener {

    private final EventOutcomeService eventOutcomeService;

    @KafkaListener(
            topics = "${spring.kafka.template.default-topic:event-outcomes}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onEventOutcome(EventOutcomeDto dto) {
        log.info("Received event outcome: {}", dto);
        eventOutcomeService.handleEventOutcome(dto);
    }
}
