package org.example.paymentservice.infrastructure.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.event.PaymentCreatedEvent;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.port.out.PaymentRepositoryPort;
import org.example.paymentservice.domain.port.out.WebhookClientPort;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.example.paymentservice.infrastructure.adapter.external.WebhookClientResponse;
import org.example.paymentservice.infrastructure.config.RABBITMQ_CONSTANTS;
import org.example.paymentservice.presentation.dto.WebhookResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final WebhookRepositoryPort webhookRepositoryPort;
    private final WebhookClientPort webhookClientPort;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RABBITMQ_CONSTANTS.PAYMENT_QUEUE)
    public void consumePaymentCreated(PaymentCreatedEvent event) {
        try {
            log.info("📥 Received payment event: paymentId={}", event.getAggregateId());

            List<Webhook> activeWebhooks = webhookRepositoryPort.findByActiveTrue();

            if (activeWebhooks.isEmpty()) {
                log.warn("No active webhooks");
                return;
            }
            log.info("Notifying {} active webhooks: [{}]", activeWebhooks.size(), activeWebhooks);

            String payload = objectMapper.writeValueAsString(event);
            for (Webhook webhook : activeWebhooks) {
                sendWebhook(webhook, payload);
            }
        } catch (Exception e) {
            log.error("Error processing payment event", e);

            // runtime triggers retry
            throw new RuntimeException("Failed to process payment event", e);
        }
    }

    private void sendWebhook(Webhook webhook, String payload) {
        try {
            WebhookClientResponse response = webhookClientPort.sendWebhook(
                    webhook.getUrl(),
                    payload,
                    webhook.getSecret()
            );

            if (!response.success()) {
                log.error(" Webhook failed url [{}], status [{}], error [{}]",
                        webhook.getUrl(), response.statusCode(), response.errorMessage());
                // retry
                throw new RuntimeException("Failed to call webhook: " + response.errorMessage());
            }

        } catch (Exception e) {
            log.error("Exception sending webhook to [{}] error [{}]", webhook.getUrl(), e);
            throw new RuntimeException("Webhook delivery exception", e);
        }
    }
}
