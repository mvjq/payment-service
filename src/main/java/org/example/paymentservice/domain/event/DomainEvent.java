package org.example.paymentservice.domain.event;


import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    UUID getPaymentId();
    String getAggregateType();
    String getEventType();
    LocalDateTime getOccurredAt();
    Object getEventData();
}
