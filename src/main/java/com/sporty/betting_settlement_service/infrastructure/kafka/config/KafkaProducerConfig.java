package com.sporty.betting_settlement_service.infrastructure.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.betting_settlement_service.api.dto.EventOutcomeDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, EventOutcomeDto> producerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, EventOutcomeDto> kafkaTemplate(ProducerFactory<String, EventOutcomeDto> pf) {
        KafkaTemplate<String, EventOutcomeDto> tpl = new KafkaTemplate<>(pf);
        tpl.setProducerListener(new ProducerListener<>() {
            @Override
            public void onSuccess(ProducerRecord<String, EventOutcomeDto> record, RecordMetadata meta) {
                log.info("Kafka write OK topic={} partition={} offset={} key={} value={}",
                        record.topic(), meta.partition(), meta.offset(), record.key(), record.value());
            }

            @Override
            public void onError(ProducerRecord<String, EventOutcomeDto> record, RecordMetadata meta, Exception ex) {
                log.error("Kafka write FAILED topic={} key={} value={}",
                        record.topic(), record.key(), record.value(), ex);
            }
        });
        return tpl;
    }
}

