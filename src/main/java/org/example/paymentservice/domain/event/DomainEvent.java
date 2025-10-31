package org.example.paymentservice.domain.event;


import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    UUID getAggregateId();
    String getAggregateType();
    String getEventType();
    LocalDateTime getOccurredAt();
    Object getEventData();
}
