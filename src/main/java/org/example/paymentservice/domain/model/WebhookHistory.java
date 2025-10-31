package org.example.paymentservice.domain.model;

import lombok.*;
import org.example.paymentservice.domain.model.valueobject.WebhookStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookHistory {
    private UUID id;
    private UUID webhookId;
    private UUID paymentId;
    private UUID eventId;
    private WebhookStatus status;
    private Integer attemptNumber;
    private String requestUrl;
    private String requestPayload;
    private Integer responseStatusCode;
    private String responseBody;
    private String errorMessage;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void sending() {
        this.status = WebhookStatus.SENDING;
        this.updatedAt = LocalDateTime.now();
    }

    public void success(Integer statusCode, String responseBody) {
        this.status = WebhookStatus.SUCCESS;
        this.responseStatusCode = statusCode;
        this.responseBody = responseBody;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void failed(String errorMessage, Integer statusCode) {
        this.status = WebhookStatus.FAILED;
        this.errorMessage = errorMessage;
        this.responseStatusCode = statusCode;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void dlq(String reason) {
        this.status = WebhookStatus.DLQ;
        this.errorMessage = reason;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void retryDeliveryWithErrorMessage(String errorMessage) {
        this.status = WebhookStatus.PENDING;
        this.attemptNumber++;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canRetry(int maxAttempts) {
        return this.attemptNumber < maxAttempts;
    }
}
