package com.sporty.betting_settlement_service.api.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EventOutcomeDto(
    UUID eventId,
    String eventName,
    String eventWinnerId
) {}
