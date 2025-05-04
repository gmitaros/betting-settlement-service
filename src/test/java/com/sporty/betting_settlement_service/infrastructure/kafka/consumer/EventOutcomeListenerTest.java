package com.sporty.betting_settlement_service.infrastructure.kafka.consumer;

import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import com.sporty.betting_settlement_service.application.service.EventOutcomeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventOutcomeListenerTest {

    @Mock
    private EventOutcomeService service;

    @InjectMocks
    private EventOutcomeListener listener;

    @Test
    void onEventOutcome_shouldDelegateToService() {
        EventOutcomeDto dto = new EventOutcomeDto(
                UUID.fromString("eeeeeeee-0000-0000-0000-eeeeeeeeeeee"),
                "Team A vs Team B",
                "TeamA"
        );

        listener.onEventOutcome(dto);

        verify(service).handleEventOutcome(dto);
    }
}
