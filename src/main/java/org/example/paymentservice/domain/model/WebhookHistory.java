package org.example.paymentservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "webhook_id", nullable = false)
    private UUID webhookId;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "event_id")
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private WebhookStatus status;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "request_url", nullable = false, length = 500)
    private String requestUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", nullable = false, columnDefinition = "jsonb")
    private String requestPayload;

    @Column(name = "response_status_code")
    private Integer responseStatusCode;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        scheduledAt = LocalDateTime.now();
        attemptNumber = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void sending() {
        this.status = WebhookStatus.SENDING;
        this.sentAt = LocalDateTime.now();
    }

    public void success(Integer statusCode, String responseBody) {
        this.status = WebhookStatus.SUCCESS;
        this.responseStatusCode = statusCode;
        this.responseBody = responseBody;
        this.completedAt = LocalDateTime.now();
    }

    public void failed(String errorMessage, Integer statusCode) {
        this.status = WebhookStatus.FAILED;
        this.errorMessage = errorMessage;
        this.responseStatusCode = statusCode;
        this.completedAt = LocalDateTime.now();
    }

    public void dlq(String reason) {
        this.status = WebhookStatus.DLQ;
        this.errorMessage = reason;
        this.completedAt = LocalDateTime.now();
    }

    public void retry() {
        this.status = WebhookStatus.PENDING;
        this.attemptNumber++;
        this.scheduledAt = LocalDateTime.now().plusSeconds(attemptNumber * 10L);
    }

    public boolean canRetry(int maxAttempts) {
        return this.attemptNumber < maxAttempts;
    }
}