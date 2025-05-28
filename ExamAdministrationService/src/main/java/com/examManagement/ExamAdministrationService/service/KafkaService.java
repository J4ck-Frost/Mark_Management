package com.examManagement.ExamAdministrationService.service;

import examManagement.common.dto.ExamEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, ExamEvent> kafkaTemplate;

    public void sendKafkaEvent(String examId, String topic) {
        ExamEvent.EventType eventType;
        if ("exam-cancellation-events".equals(topic)){
            eventType = ExamEvent.EventType.CANCELLATION;
        }
        else if ("exam-publication-events".equals(topic))
            eventType = ExamEvent.EventType.PUBLICATION;
        else
            eventType = ExamEvent.EventType.COMPLETION;

        ExamEvent event = new ExamEvent(
                examId,
                eventType,
                LocalDateTime.now()
        );
        try {
            kafkaTemplate.send(topic, event);
            log.info("✅ Kafka event sent successfully to topic {} with data: {}", topic, event);
        } catch (Exception ex) {
            log.error("❌ Failed to send Kafka event to topic {}. Error: {}", topic, ex.getMessage(), ex);
        }
    }
}
