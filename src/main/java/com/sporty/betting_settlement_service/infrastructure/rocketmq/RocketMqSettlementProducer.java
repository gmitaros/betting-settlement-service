package com.sporty.betting_settlement_service.infrastructure.rocketmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.betting_settlement_service.api.dto.BetSettlementDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMqSettlementProducer {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${rocketmq.producer.topic}")
    private String topic;

    private final ObjectMapper objectMapper;
    private DefaultMQProducer producer;


    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(10_000);
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        log.info("RocketMQ Producer started (group='{}', topic='{}')", producerGroup, topic);
    }

    /**
     * Sends a BetSettlementDto as JSON to the configured topic.
     */
    public void sendSettlement(BetSettlementDto settlement) {
        try {
            byte[] body = objectMapper.writeValueAsBytes(settlement);
            Message msg = new Message(topic, body);
            SendResult result = producer.send(msg);
            log.info("Sent settlement (betId={}) to RocketMQ: {}", settlement.betId(), result);
        } catch (Exception e) {
            log.error("Failed to send settlement (betId={}) to RocketMQ", settlement.betId(), e);
        }
    }
}
