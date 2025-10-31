package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.model.WebhookHistory;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookHistoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookHistoryRepositoryPort {
    WebhookHistory save(WebhookHistory webhookHistory);
    Optional<WebhookHistory> findByWebhookId(UUID id);
    List<WebhookHistory> findByPaymentId(UUID id);
    List<WebhookHistory> findAll();;
}

