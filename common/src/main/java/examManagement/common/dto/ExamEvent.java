package examManagement.common.dto;

import java.time.LocalDateTime;

public record ExamEvent(
        String examId,
        EventType eventType,
        LocalDateTime timestamp
) {
    public enum EventType {
        PUBLICATION, COMPLETION, CANCELLATION
    }
}
