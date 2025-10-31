package org.example.paymentservice.infrastructure.adapter.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.event.DomainEvent;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.OutboxEventEntity;
import org.example.paymentservice.domain.port.out.EventStorePort;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.OutboxJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventStore implements EventStorePort {

    private final OutboxJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void store(DomainEvent event) {
        try {
            OutboxEventEntity outboxEventEntity = OutboxEventEntity.from(event, objectMapper);
            outboxRepository.save(outboxEventEntity);
            log.info("Persisted event to outbox: eventType=[{}], aggregateId=[{}]",
                    event.getEventType(), event.getAggregateId());
        } catch (Exception e) {
            // TODO implement custom exception
            throw new RuntimeException("Failed to publish event to outbox", e);
        }
    }
}
