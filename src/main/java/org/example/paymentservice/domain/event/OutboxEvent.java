package org.example.paymentservice.domain.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {
    private UUID id;
    private UUID aggregateId;
    private String aggregateType;
    private String eventType;
    private String payload;
    private Boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private Integer retryCount;
    private String errorMessage;

    public void published() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void incrementRetryWithMessage(String message) {
        this.retryCount++;
        this.errorMessage = message;
    }

    public boolean canRetry(int maxRetries) {
        return this.retryCount < maxRetries;
    }
    public boolean isPublished() {
        return Boolean.TRUE.equals(this.published);
    }
}
