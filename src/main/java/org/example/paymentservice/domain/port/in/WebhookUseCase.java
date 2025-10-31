package org.example.paymentservice.domain.port.in;

import jakarta.transaction.Transactional;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.model.WebhookHistory;

import java.util.List;
import java.util.UUID;

public interface WebhookUseCase {
    Webhook execute(WebhookCommand command);
    Webhook update(UUID uuid, Webhook webhook);
    void deleteWebhook(UUID uuid);
    void disableWebhook(UUID uuid);
    Webhook findWebhook(UUID uuid);
    List<Webhook> findAllWebhooks();
    List<WebhookHistory> findWebhookHistory(UUID webhookId);
    List<WebhookHistory> findPaymentWebhookHistory(UUID paymentId);
}
