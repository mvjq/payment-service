package org.example.paymentservice.infrastructure.adapter.persistence.repository;

import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WebhookJpaRepository extends JpaRepository<WebhookEntity, UUID> {
    Optional<WebhookEntity> findByPaymentId(UUID paymentId);
}
