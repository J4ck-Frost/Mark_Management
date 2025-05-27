package examManagement.common.dto;

import java.time.LocalDateTime;

public record CandidateEvent(
        String candidateId,
        String examId,
        EventType eventType,
        LocalDateTime timestamp
) {
    public enum EventType {
        REGISTRATION, UNREGISTRATION
    }
}
