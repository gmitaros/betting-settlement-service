package com.sporty.betting_settlement_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import com.sporty.betting_settlement_service.infrastructure.kafka.producer.EventOutcomeProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventOutcomeController.class)
class EventOutcomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EventOutcomeProducer producer;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void publishValidDtoReturns202AndSends() throws Exception {
        EventOutcomeDto dto = new EventOutcomeDto(
                UUID.fromString("eeeeeeee-0000-0000-0000-eeeeeeeeeeee"),
                "Team A vs Team B",
                "TeamA"
        );

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/events/outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().string("")); // no body

        // verify it went to your producer
        verify(producer).sendAsync(dto);
    }

    @Test
    void publishMalformedJsonReturns400AndDoesNotSend() throws Exception {
        String badJson = "{\"eventId\": \"not-a-uuid\", \"eventName\": 123 }";

        mockMvc.perform(post("/api/v1/events/outcomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());

        verify(producer, never()).sendAsync(org.mockito.ArgumentMatchers.any());
    }
}
