package org.example.paymentservice.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.event.PaymentCreatedEvent;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.port.out.WebhookClientPort;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.example.paymentservice.infrastructure.adapter.external.WebhookClientResponse;
import org.example.paymentservice.infrastructure.config.RABBITMQ_CONSTANTS;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final WebhookRepositoryPort webhookRepositoryPort;
    private final WebhookClientPort webhookClientPort;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RABBITMQ_CONSTANTS.PAYMENT_QUEUE)
    public void consumePaymentCreated(PaymentCreatedEvent event) {
        try {
            log.info("Received payment event: paymentId={}", event.getPaymentId());

            Optional<Webhook> webhook = webhookRepositoryPort.findById(event.getWebhookId());

            if (webhook.isEmpty() || !webhook.get().canReceiveEvents()) {
                log.warn("No active webhooks");
                return;
            }
            log.info("Notifying webhooks: [{}]", webhook);
            String payload = objectMapper.writeValueAsString(event);
            sendWebhook(webhook.get(), payload);

        } catch (Exception e) {
            log.error("Error processing payment event", e);

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
