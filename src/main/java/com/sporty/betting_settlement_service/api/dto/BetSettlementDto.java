package com.sporty.betting_settlement_service.api.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BetSettlementDto(
        UUID betId,
        UUID userId,
        UUID eventId
) {
}
