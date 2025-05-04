package com.sporty.betting_settlement_service.application.service;

import com.sporty.betting_settlement_service.api.dto.BetSettlementDto;
import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import com.sporty.betting_settlement_service.domain.model.Bet;
import com.sporty.betting_settlement_service.infrastructure.repository.BetRepository;
import com.sporty.betting_settlement_service.infrastructure.rocketmq.RocketMqSettlementProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@DataJpaTest(
        excludeAutoConfiguration = {KafkaAutoConfiguration.class},
        properties = {"spring.jpa.hibernate.ddl-auto=create-drop"}
)
@Import(EventOutcomeService.class)
@DirtiesContext
class EventOutcomeServiceTest {

    @Autowired
    BetRepository betRepository;

    @Autowired
    EventOutcomeService eventOutcomeService;

    @MockitoBean
    RocketMqSettlementProducer rocketMqProducer;

    private final UUID EVENT_ID = UUID.fromString("eeeeeeee-0000-0000-0000-eeeeeeeeeeee");

    @BeforeEach
    void setUp() {
        betRepository.deleteAll();

        // two unsettled bets
        betRepository.save(Bet.builder()
                .userId(UUID.fromString("00000000-1111-2222-3333-555555555555"))
                .eventId(EVENT_ID)
                .eventMarketId("market-01")
                .eventWinnerId("TeamA")
                .betAmount(new BigDecimal("100.00"))
                .build());

        betRepository.save(Bet.builder()
                .userId(UUID.fromString("00000000-1111-2222-3333-777777777777"))
                .eventId(EVENT_ID)
                .eventMarketId("market-01")
                .eventWinnerId("TeamB")
                .betAmount(new BigDecimal("50.00"))
                .build());
    }

    @Test
    void whenHandleEventOutcome_thenPublishOneSettlementPerBet() {
        // given
        EventOutcomeDto dto = new EventOutcomeDto(
                EVENT_ID,
                "Team A vs Team B",
                "TeamA"
        );

        // when
        eventOutcomeService.handleEventOutcome(dto);

        // then
        // we expect exactly 2 calls (one per row in bets)
        verify(rocketMqProducer, times(2)).sendSettlement(any());

        // capture arguments to assert correctness
        var captor = ArgumentCaptor.forClass(BetSettlementDto.class);
        verify(rocketMqProducer, times(2)).sendSettlement(captor.capture());

        List<BetSettlementDto> sent = captor.getAllValues();
        // check that each DTO refers to one of our saved betIds
        List<UUID> savedIds = betRepository.findByEventId(EVENT_ID)
                .stream().map(Bet::getBetId).toList();
        assertThat(sent)
                .extracting(BetSettlementDto::betId)
                .containsExactlyInAnyOrderElementsOf(savedIds);
    }

    @Test
    void whenHandleUnknownEvent_thenNoPublish() {
        // GIVEN an outcome for a different event
        EventOutcomeDto dto = new EventOutcomeDto(
                UUID.randomUUID(),
                "Some other match",
                "TeamX"
        );

        // WHEN
        eventOutcomeService.handleEventOutcome(dto);

        // THEN
        // no bets exist for that event → nothing published
        verifyNoInteractions(rocketMqProducer);
    }
}