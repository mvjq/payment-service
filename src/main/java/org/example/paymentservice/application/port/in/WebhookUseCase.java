package org.example.paymentservice.application.port.in;

import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.domain.model.Webhook;

import java.util.List;
import java.util.UUID;

public interface WebhookUseCase {
    Webhook create(WebhookCommand command);
    Webhook update(UUID uuid, WebhookCommand command);
    void deleteWebhook(UUID uuid);
    void disableWebhook(UUID uuid);
    Webhook findWebhookByPaymentId(UUID paymentId);
    List<Webhook> findAllWebhooks();
}
