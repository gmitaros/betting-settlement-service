package com.sporty.betting_settlement_service.api.controller;

import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import com.sporty.betting_settlement_service.infrastructure.kafka.producer.EventOutcomeProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events/outcomes")
@RequiredArgsConstructor
public class EventOutcomeController {

    private final EventOutcomeProducer producer;


    @PostMapping
    public ResponseEntity<Void> publish(@RequestBody EventOutcomeDto dto) {
        producer.sendAsync(dto);
        return ResponseEntity.accepted().build();
    }
}
