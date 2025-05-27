package com.examManagement.CandidateManagementService.service;

import examManagement.common.dto.CandidateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, CandidateEvent> kafkaTemplate;

    public void sendKafkaEvent(String candidateId, String examId, String topic) {
        CandidateEvent.EventType eventType;
        if ("exam-registration-events".equals(topic)){
            eventType = CandidateEvent.EventType.REGISTRATION;
        }
        else eventType = CandidateEvent.EventType.UNREGISTRATION;
        CandidateEvent event = new CandidateEvent(
                candidateId,
                examId,
                eventType,
                LocalDateTime.now()
        );

        try {
            kafkaTemplate.send(topic, event);
            log.info("✅ Sent Kafka event to topic {}: {}", topic, event);
        } catch (Exception ex) {
            log.error("❌ Failed to send Kafka event to topic {}. Error: {}", topic, ex.getMessage(), ex);
        }
    }
}
