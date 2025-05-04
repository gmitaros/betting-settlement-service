package com.sporty.betting_settlement_service.infrastructure.repository;

import com.sporty.betting_settlement_service.domain.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BetRepository extends JpaRepository<Bet, UUID> {

    List<Bet> findByEventId(UUID eventId);

}
