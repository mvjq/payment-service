package org.example.paymentservice.infrastructure.adapter.persistence.entity;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.example.paymentservice.domain.event.DomainEvent;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "webhook_id")
    private UUID webhookId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        published = false;
        retryCount = 0;
    }

    public void published() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    public static OutboxEventEntity from(DomainEvent event, ObjectMapper objectMapper) {
         try {
            String jsonPayload = objectMapper.writeValueAsString(event);

            return OutboxEventEntity.builder()
                    .paymentId(event.getPaymentId())
                    .eventType(event.getEventType())
                    .webhookId(event.getWebhookId())
                    .payload(jsonPayload)
                    .published(false)
                    .retryCount(0)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }

    public void incrementRetryWithMessage(String message) {
        this.retryCount++;
        this.errorMessage = message;
    }
}