package org.example.paymentservice.infrastructure.adapter.messaging;

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
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherSchedulerTest {

    public static final String PAYMENT_CREATED = "PAYMENT_CREATED";
    public static final String EVENT_ID = "{\"eventId\":\"123\"}";
    @Mock(strictness = Mock.Strictness.LENIENT)
    private OutboxJpaRepository outboxRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RabbitTemplate rabbitTemplate;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxPublisherScheduler scheduler;

    private OutboxEventEntity outboxEvent;

    @BeforeEach
    void setUp() throws Exception {
        outboxEvent = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .paymentId(UUID.randomUUID())
                .webhookId(UUID.randomUUID())
                .eventType(PAYMENT_CREATED)
                .payload(EVENT_ID)
                .published(false)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        PaymentCreatedEvent event = PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(UUID.randomUUID())
                .webhookId(UUID.randomUUID())
                .occurredAt(LocalDateTime.now())
                .build();

        when(objectMapper.readValue(anyString(), eq(PaymentCreatedEvent.class))).thenReturn(event);
    }

    @Test
    void shouldPublishUnpublishedEvents() {
        when(outboxRepository.findUnpublishedEventsWithRetryLimit(anyInt(), anyInt()))
                .thenReturn(List.of(outboxEvent));

        scheduler.publishPendingEvents();

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PaymentCreatedEvent.class));
        verify(outboxRepository).save(argThat(event -> event.getPublished()));
    }

    @Test
    void shouldNotPublishWhenNoEvents() {
        when(outboxRepository.findUnpublishedEventsWithRetryLimit(anyInt(), anyInt()))
                .thenReturn(List.of());

        scheduler.publishPendingEvents();

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void shouldIncrementRetryCountOnFailure() throws Exception {
        when(outboxRepository.findUnpublishedEventsWithRetryLimit(anyInt(), anyInt()))
                .thenReturn(List.of(outboxEvent));
        doThrow(new RuntimeException("RabbitMQ connection error"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(PaymentCreatedEvent.class));

        scheduler.publishPendingEvents();

        verify(outboxRepository).save(argThat(event -> 
            event.getRetryCount() == 1 && event.getErrorMessage() != null
        ));
    }
}
