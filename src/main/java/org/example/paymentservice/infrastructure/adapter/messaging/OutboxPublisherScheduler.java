package org.example.paymentservice.infrastructure.adapter.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.event.PaymentCreatedEvent;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.OutboxEventEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.OutboxJpaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final OutboxJpaRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String EXCHANGE = "payment.events";
    private static final String ROUTING_KEY = "payment.created";
    private static final int MAX_RETRIES = 3;
    private static final int BATCH_SIZE = 100;

    // 10 seconds
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> unpublishedEvents = outboxRepository.findUnpublishedEventsWithRetryLimit(MAX_RETRIES, BATCH_SIZE);

        if (unpublishedEvents.isEmpty()) {
            return;
        }

        for (OutboxEventEntity outboxEventEntity : unpublishedEvents) {
            try {
                publishToRabbitMQ(outboxEventEntity);
                outboxEventEntity.published();
                outboxRepository.save(outboxEventEntity);
                
                log.info("Successfully published event: eventId=[{}], eventType=[{}]",
                        outboxEventEntity.getId(), outboxEventEntity.getEventType());

            } catch (Exception e) {
                log.error("Failed to publish event: eventId=[{}], attempt=[{}]",
                        outboxEventEntity.getId(), outboxEventEntity.getRetryCount() + 1, e);

                outboxEventEntity.incrementRetryWithMessage(e.getMessage());
                outboxRepository.save(outboxEventEntity);

                if (outboxEventEntity.getRetryCount() >= MAX_RETRIES) {
                    log.error("Event reached max retries and will not be retried: eventId={}", 
                            outboxEventEntity.getId());
                }
            }
        }
    }

    private void publishToRabbitMQ(OutboxEventEntity outboxEventEntity) throws Exception {
        Object event = deserializeEvent(outboxEventEntity);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
        log.debug("Published to RabbitMQ: exchange=[{}], routingKey=[{}], eventType=[{}]",
                EXCHANGE, ROUTING_KEY, outboxEventEntity.getEventType());
    }

    private Object deserializeEvent(OutboxEventEntity outboxEventEntity) throws Exception {
        return switch (outboxEventEntity.getEventType()) {
            case "PAYMENT_CREATED" -> objectMapper.readValue(outboxEventEntity.getPayload(), PaymentCreatedEvent.class);
            default -> throw new IllegalArgumentException("Unknown event type: " + outboxEventEntity.getEventType());
        };
    }
}
