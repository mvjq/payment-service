package org.example.paymentservice.application.port.in;

import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.model.WebhookHistory;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;

import java.util.List;
import java.util.UUID;

public interface WebhookUseCase {
    Webhook execute(WebhookCommand command);
    Webhook update(UUID uuid, WebhookEntity webhookEntity);
    void deleteWebhook(UUID uuid);
    void disableWebhook(UUID uuid);
    Webhook findWebhookByPaymentUUID(UUID paymentId);
    List<Webhook> findAllWebhooks();
    List<WebhookHistory> findWebhookHistory(UUID webhookId);
    List<WebhookHistory> findWebhookHistoryByPaymentUUID(UUID paymentId);
}
