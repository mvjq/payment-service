package org.example.paymentservice.infrastructure.adapter.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paymentservice.domain.event.PaymentCreatedEvent;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.OutboxEventEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.OutboxJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventStoreTest {

    public static final String EVENT_ID = "{\"eventId\":\"123\"}";
    @Mock
    private OutboxJpaRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxEventStore outboxEventStore;

    private PaymentCreatedEvent event;

    @BeforeEach
    void setUp() {
        event = PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(UUID.randomUUID())
                .webhookId(UUID.randomUUID())
                .occurredAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldStoreEventSuccessfully() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn(EVENT_ID);
        when(outboxRepository.save(any(OutboxEventEntity.class))).thenReturn(new OutboxEventEntity());

        outboxEventStore.store(event);

        verify(outboxRepository).save(any(OutboxEventEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenStoreFails() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("Serialization error"));

        assertThrows(RuntimeException.class, () -> 
            outboxEventStore.store(event)
        );

        verify(outboxRepository, never()).save(any());
    }
}
