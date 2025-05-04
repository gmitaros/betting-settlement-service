package com.sporty.betting_settlement_service.application.service;

import com.sporty.betting_settlement_service.api.dto.BetSettlementDto;
import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import com.sporty.betting_settlement_service.domain.model.Bet;
import com.sporty.betting_settlement_service.infrastructure.repository.BetRepository;
import com.sporty.betting_settlement_service.infrastructure.rocketmq.RocketMqSettlementProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventOutcomeService {

    private final BetRepository betRepository;
    private final RocketMqSettlementProducer rocketMqProducer;

    /**
     * Handles an incoming event outcome: settles all matching bets.
     * <p>
     * 1. Load bets for the event.
     * 2. For each bet:
     * - Emit BetSettlementDto to RocketMQ.
     */
    @Transactional(readOnly = true)
    public void handleEventOutcome(EventOutcomeDto dto) {
        List<Bet> bets = betRepository.findByEventId(dto.eventId());
        log.info("Found {} bets for event {}", bets.size(), dto.eventId());

        bets.parallelStream().forEach(bet -> {
            BetSettlementDto settlement = BetSettlementDto.builder()
                    .betId(bet.getBetId())
                    .userId(bet.getUserId())
                    .eventId(bet.getEventId())
                    .build();

            rocketMqProducer.sendSettlement(settlement);
            log.debug("Published settlement for bet {}", bet.getBetId());
        });
    }
}
